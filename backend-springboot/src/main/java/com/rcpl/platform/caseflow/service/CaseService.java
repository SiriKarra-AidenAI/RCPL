package com.rcpl.platform.caseflow.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.rcpl.platform.audit.AuditService;
import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.candidate.Candidate;
import com.rcpl.platform.candidate.CandidateRepository;
import com.rcpl.platform.caseflow.dto.CaseDtos.CaseDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.ChannelSnapshotDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.FinanceDocDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.FinanceSnapshotDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.LeadershipNoteDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.RaiseCaseRequest;
import com.rcpl.platform.caseflow.entity.CaseChannelDoc;
import com.rcpl.platform.caseflow.entity.CaseChannelSnapshot;
import com.rcpl.platform.caseflow.entity.CaseEntity;
import com.rcpl.platform.caseflow.entity.CaseFinanceDoc;
import com.rcpl.platform.caseflow.entity.CaseFinanceSnapshot;
import com.rcpl.platform.caseflow.entity.CaseLeadershipNote;
import com.rcpl.platform.caseflow.repository.CaseChannelDocRepository;
import com.rcpl.platform.caseflow.repository.CaseChannelSnapshotRepository;
import com.rcpl.platform.caseflow.repository.CaseFinanceDocRepository;
import com.rcpl.platform.caseflow.repository.CaseFinanceSnapshotRepository;
import com.rcpl.platform.caseflow.repository.CaseLeadershipNoteRepository;
import com.rcpl.platform.caseflow.repository.CaseRepository;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.DateLabels;
import com.rcpl.platform.common.JsonSupport;
import com.rcpl.platform.partner.PartnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The approvals workflow — a deterministic state machine ported from store.ts:
 * raise (upsert by candidate+owner, dual-fail siblings), decide (Finance/Channel → Leadership
 * hand-off, replacement gate, activate-partner on sign-off), and case attachments/notes that
 * propagate across sibling cases. Every state change writes an audit row in the same transaction.
 */
@Service
public class CaseService {

    private static final String FINANCE = "finance";
    private static final String CHANNEL = "channel_dev";
    private static final String LEADERSHIP = "leadership";

    private final CaseRepository caseRepository;
    private final CaseFinanceSnapshotRepository financeSnapshotRepo;
    private final CaseChannelSnapshotRepository channelSnapshotRepo;
    private final CaseFinanceDocRepository financeDocRepo;
    private final CaseChannelDocRepository channelDocRepo;
    private final CaseLeadershipNoteRepository noteRepo;
    private final CandidateRepository candidateRepository;
    private final PartnerService partnerService;
    private final AuditService auditService;
    private final CaseCodeService caseCodeService;
    private final SlaService slaService;
    private final JsonSupport json;

    public CaseService(CaseRepository caseRepository, CaseFinanceSnapshotRepository financeSnapshotRepo,
                       CaseChannelSnapshotRepository channelSnapshotRepo, CaseFinanceDocRepository financeDocRepo,
                       CaseChannelDocRepository channelDocRepo, CaseLeadershipNoteRepository noteRepo,
                       CandidateRepository candidateRepository, PartnerService partnerService,
                       AuditService auditService, CaseCodeService caseCodeService, SlaService slaService,
                       JsonSupport json) {
        this.caseRepository = caseRepository;
        this.financeSnapshotRepo = financeSnapshotRepo;
        this.channelSnapshotRepo = channelSnapshotRepo;
        this.financeDocRepo = financeDocRepo;
        this.channelDocRepo = channelDocRepo;
        this.noteRepo = noteRepo;
        this.candidateRepository = candidateRepository;
        this.partnerService = partnerService;
        this.auditService = auditService;
        this.caseCodeService = caseCodeService;
        this.slaService = slaService;
        this.json = json;
    }

    @Transactional(readOnly = true)
    public List<CaseDto> list(CurrentUser user, boolean mineOnly) {
        List<CaseEntity> all = caseRepository.findAll();
        return all.stream()
                .filter(c -> !mineOnly || isInQueue(user, c))
                .map(this::toDto)
                .toList();
    }

    private boolean isInQueue(CurrentUser user, CaseEntity c) {
        return user.roleCode().equals(c.getOwnerRole())
                || (c.getInvolvedRoles() != null && c.getInvolvedRoles().contains(user.roleCode()));
    }

    @Transactional(readOnly = true)
    public CaseDto get(String code) {
        return toDto(require(code));
    }

    /** Raise a flagged case (upsert by candidateId + ownerRole, mirroring flagCandidateCase). */
    @Transactional
    public CaseDto raise(CurrentUser user, RaiseCaseRequest req) {
        CaseEntity target = null;
        if (req.candidateId() != null) {
            target = caseRepository.findByCandidateId(req.candidateId()).stream()
                    .filter(c -> req.ownerRole().equals(c.getOwnerRole()))
                    .findFirst().orElse(null);
        }
        boolean isNew = target == null;
        CaseEntity c = isNew ? new CaseEntity() : target;
        if (isNew) {
            c.setCode(caseCodeService.next(req.partnerType()));
            c.setCreatedAt(Instant.now());
        }
        c.setPartnerName(req.partnerName());
        c.setPartnerType(req.partnerType());
        c.setTown(req.town());
        c.setState(req.state());
        c.setSubtype(req.subtype() == null ? "new" : req.subtype());
        c.setStatus("flagged");
        c.setOwnerRole(req.ownerRole());
        c.setSignoffAuthority(req.signoffAuthority() == null ? "SM" : req.signoffAuthority());
        c.setSlaLabel(slaService.label(req.slaHours() > 0 ? req.slaHours() : 24));
        c.setOverdue(false);
        c.setHasDiscontinuationForm(req.hasDiscontinuationForm());
        if (req.discontinuationForm() != null) {
            c.setDiscontinuationForm(json.write(req.discontinuationForm()));
        }
        c.setConfidencePct(req.confidencePct());
        c.setFlagDetail(req.flagDetail());
        c.setCandidateId(req.candidateId());
        c.setUpdatedAt(Instant.now());
        c.getInvolvedRoles().add(req.ownerRole());
        caseRepository.save(c);

        if (req.financeSnapshot() != null) saveFinanceSnapshot(c.getId(), req.financeSnapshot());
        if (req.channelSnapshot() != null) saveChannelSnapshot(c.getId(), req.channelSnapshot());

        auditService.logHuman(user.name(), isNew ? "Raised case" : "Updated case", c.getCode());
        return toDto(c);
    }

    /** Approve/reject with the full hand-off, gate, and activation logic. */
    @Transactional
    public CaseDto decide(CurrentUser user, String code, String decision) {
        CaseEntity target = require(code);

        if ("rejected".equals(decision)) {
            target.setStatus("rejected");
            target.setUpdatedAt(Instant.now());
            caseRepository.save(target);
            auditService.logHuman(user.name(), "Rejected case", code);
            return toDto(target);
        }
        if (!"approved".equals(decision)) {
            throw new ApiException.BadRequest("decision must be 'approved' or 'rejected'");
        }

        // Replacement → Discontinuation gate.
        if ("replacement".equals(target.getSubtype()) && !target.isHasDiscontinuationForm()) {
            throw new ApiException.Conflict("gate_blocked",
                    "Replacement case requires a linked discontinuation form before approval");
        }

        boolean siblingStillOpen = target.getCandidateId() != null
                && caseRepository.findByCandidateId(target.getCandidateId()).stream()
                .anyMatch(c -> !c.getCode().equals(code)
                        && "flagged".equals(c.getStatus())
                        && (FINANCE.equals(c.getOwnerRole()) || CHANNEL.equals(c.getOwnerRole())));

        boolean ownerIsChecker = FINANCE.equals(target.getOwnerRole()) || CHANNEL.equals(target.getOwnerRole());
        if (ownerIsChecker && !siblingStillOpen) {
            String authority = target.getSignoffAuthority() == null ? "SM" : target.getSignoffAuthority();
            target.setOwnerRole(LEADERSHIP);
            target.setFlagDetail("Financial & infra checks clear — routed to " + authority + " for final sign-off.");
            target.getInvolvedRoles().add(LEADERSHIP);
            target.setUpdatedAt(Instant.now());
            caseRepository.save(target);
            auditService.logHuman(user.name(), "Cleared check, routed to " + authority, code);
            return toDto(target);
        }

        // Terminal: Leadership sign-off (or an MDM/checker with a still-open sibling closing its own).
        boolean becomesActive = LEADERSHIP.equals(target.getOwnerRole()) && target.getCandidateId() != null;
        target.setStatus("approved");
        target.setUpdatedAt(Instant.now());
        caseRepository.save(target);

        if (becomesActive) {
            candidateRepository.findById(target.getCandidateId()).ifPresent(cd -> {
                cd.setStage("active");
                candidateRepository.save(cd);
            });
            partnerService.upsertActivePartner(target.getCandidateId(), target.getPartnerName(),
                    target.getPartnerType(), target.getState(), target.getTown());
        }
        auditService.logHuman(user.name(), "Approved case", code);
        return toDto(target);
    }

    @Transactional
    public CaseDto attachFinanceDoc(CurrentUser user, String code, String key, String fileName, String dataUrl) {
        CaseEntity target = require(code);
        for (CaseEntity c : siblings(target)) {
            CaseFinanceDoc doc = financeDocRepo.findByCaseIdAndDocKey(c.getId(), key)
                    .orElseGet(() -> {
                        CaseFinanceDoc d = new CaseFinanceDoc();
                        d.setCaseId(c.getId());
                        d.setDocKey(key);
                        return d;
                    });
            doc.setFileName(fileName);
            doc.setDataUrl(dataUrl);
            financeDocRepo.save(doc);
        }
        auditService.logHuman(user.name(), "Uploaded finance document", code);
        return toDto(target);
    }

    @Transactional
    public CaseDto attachChannelDoc(CurrentUser user, String code, String fileName) {
        CaseEntity target = require(code);
        for (CaseEntity c : siblings(target)) {
            CaseChannelDoc doc = new CaseChannelDoc();
            doc.setCaseId(c.getId());
            doc.setFileName(fileName);
            channelDocRepo.save(doc);
        }
        auditService.logHuman(user.name(), "Uploaded coverage plan", code);
        return toDto(target);
    }

    @Transactional
    public CaseDto addNote(CurrentUser user, String code, String author, String body) {
        CaseEntity target = require(code);
        for (CaseEntity c : siblings(target)) {
            CaseLeadershipNote note = new CaseLeadershipNote();
            note.setCaseId(c.getId());
            note.setAuthor(author);
            note.setBody(body);
            note.setWhenLabel(DateLabels.auditStamp());
            noteRepo.save(note);
        }
        auditService.logHuman(user.name(), "Left a note for leadership", code);
        return toDto(target);
    }

    @Transactional
    public CaseDto linkDiscontinuation(CurrentUser user, String code, JsonNode form) {
        CaseEntity target = require(code);
        target.setHasDiscontinuationForm(true);
        if (form != null) target.setDiscontinuationForm(json.write(form));
        target.setUpdatedAt(Instant.now());
        caseRepository.save(target);
        auditService.logHuman(user.name(), "Linked discontinuation form", code);
        return toDto(target);
    }

    @Transactional
    public CaseDto markOnboardingNotified(String code) {
        CaseEntity target = require(code);
        target.setOnboardingNotified(true);
        caseRepository.save(target);
        return toDto(target);
    }

    // ---- helpers -----------------------------------------------------------

    private CaseEntity require(String code) {
        return caseRepository.findByCode(code)
                .orElseThrow(() -> new ApiException.NotFound("Case not found: " + code));
    }

    /** The case plus any sibling cases sharing its candidateId (dual-fail siblings). */
    private List<CaseEntity> siblings(CaseEntity target) {
        if (target.getCandidateId() == null) return List.of(target);
        List<CaseEntity> siblings = new ArrayList<>(caseRepository.findByCandidateId(target.getCandidateId()));
        if (siblings.stream().noneMatch(c -> c.getId().equals(target.getId()))) {
            siblings.add(target);
        }
        return siblings;
    }

    private void saveFinanceSnapshot(Long caseId, FinanceSnapshotDto s) {
        CaseFinanceSnapshot e = financeSnapshotRepo.findById(caseId).orElseGet(CaseFinanceSnapshot::new);
        e.setCaseId(caseId);
        e.setOwnFunds(s.ownFunds());
        e.setCcLimit(s.ccLimit());
        e.setCapitalAvailable(s.capitalAvailable());
        e.setRequiredInvestment(s.requiredInvestment());
        e.setFundingGap(s.fundingGap());
        e.setReadinessPct(s.readinessPct());
        financeSnapshotRepo.save(e);
    }

    private void saveChannelSnapshot(Long caseId, ChannelSnapshotDto s) {
        CaseChannelSnapshot e = channelSnapshotRepo.findById(caseId).orElseGet(CaseChannelSnapshot::new);
        e.setCaseId(caseId);
        e.setScore(s.score());
        e.setThreshold(s.threshold());
        e.setGap(s.gap());
        e.setReadinessPct(s.readinessPct());
        channelSnapshotRepo.save(e);
    }

    private CaseDto toDto(CaseEntity c) {
        FinanceSnapshotDto finance = financeSnapshotRepo.findById(c.getId())
                .map(f -> new FinanceSnapshotDto(f.getOwnFunds(), f.getCcLimit(), f.getCapitalAvailable(),
                        f.getRequiredInvestment(), f.getFundingGap(), f.getReadinessPct()))
                .orElse(null);
        ChannelSnapshotDto channel = channelSnapshotRepo.findById(c.getId())
                .map(ch -> new ChannelSnapshotDto(ch.getScore(), ch.getThreshold(), ch.getGap(), ch.getReadinessPct()))
                .orElse(null);

        Map<String, FinanceDocDto> financeDocs = new LinkedHashMap<>();
        for (CaseFinanceDoc d : financeDocRepo.findByCaseId(c.getId())) {
            financeDocs.put(d.getDocKey(), new FinanceDocDto(d.getFileName(), d.getDataUrl()));
        }
        List<CaseChannelDoc> channelDocs = channelDocRepo.findByCaseId(c.getId());
        String channelDoc = channelDocs.isEmpty() ? null : channelDocs.get(channelDocs.size() - 1).getFileName();

        List<LeadershipNoteDto> notes = noteRepo.findByCaseIdOrderByCreatedAtAsc(c.getId()).stream()
                .map(n -> new LeadershipNoteDto(n.getAuthor(), n.getBody(), n.getWhenLabel()))
                .toList();

        return new CaseDto(
                c.getCode(), c.getPartnerName(), c.getPartnerType(), c.getTown(), c.getState(),
                c.getSubtype(), c.getStatus(), c.getOwnerRole(), new ArrayList<>(c.getInvolvedRoles()),
                c.getSlaLabel(), c.isOverdue(), c.isHasDiscontinuationForm(),
                json.parse(c.getDiscontinuationForm()), c.getConfidencePct(), c.getCandidateId(),
                c.getFlagDetail(), c.getSignoffAuthority(), c.isOnboardingNotified(),
                finance, channel, financeDocs.isEmpty() ? null : financeDocs, channelDoc,
                notes.isEmpty() ? null : notes);
    }
}
