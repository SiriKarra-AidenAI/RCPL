package com.rcpl.platform.candidate;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.rcpl.platform.audit.AuditService;
import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.candidate.CandidateDtos.ActivateRequest;
import com.rcpl.platform.candidate.CandidateDtos.CandidateDto;
import com.rcpl.platform.candidate.CandidateDtos.CreateCandidateRequest;
import com.rcpl.platform.candidate.CandidateDtos.UpdateCandidateRequest;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.Ids;
import com.rcpl.platform.common.JsonSupport;
import com.rcpl.platform.partner.PartnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Candidate (lead) pipeline: CRUD, stage transitions, and activation to an active Partner. */
@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PartnerService partnerService;
    private final AuditService auditService;
    private final JsonSupport json;

    public CandidateService(CandidateRepository candidateRepository, PartnerService partnerService,
                            AuditService auditService, JsonSupport json) {
        this.candidateRepository = candidateRepository;
        this.partnerService = partnerService;
        this.auditService = auditService;
        this.json = json;
    }

    @Transactional(readOnly = true)
    public List<CandidateDto> list() {
        return candidateRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CandidateDto get(String id) {
        return toDto(require(id));
    }

    @Transactional
    public CandidateDto create(CurrentUser user, CreateCandidateRequest req) {
        if (user == null) throw new ApiException.BadRequest("Current user is required");
        if (req == null) throw new ApiException.BadRequest("Request body is required");
        Candidate c = new Candidate();
        c.setId(Ids.newId("c"));
        c.setName(req.name());
        c.setTown(req.town());
        c.setDbCategory(req.dbCategory());
        c.setTurnoverMonthly(req.turnoverMonthly());
        c.setExpectedRcplTurnover(req.expectedRcplTurnover());
        c.setCoverageOutlets(req.coverageOutlets());
        c.setInfraScore(req.infraScore());
        c.setFinEvalPct(req.finEvalPct());
        c.setStage(req.stage() == null ? "open" : req.stage());
        c.setConfidencePct(req.confidencePct());
        c.setUserCreated(true);
        c.setCreatedBy(user.roleCode());
        c.setCreatedAt(System.currentTimeMillis());
        c.setSourceIntakeId(req.sourceIntakeId());
        c.setSubtype(req.subtype());
        c.setOldDbCode(req.oldDbCode());
        c.setOldDbName(req.oldDbName());
        c.setAdditionalReason(req.additionalReason());
        c.setDiscontinuationForm(json.write(req.discontinuationForm()));
        candidateRepository.save(c);
        auditService.logHuman(user.name(), "Created lead", c.getName());
        return toDto(c);
    }

    @Transactional
    public CandidateDto update(String id, UpdateCandidateRequest req) {
        if (req == null) throw new ApiException.BadRequest("Request body is required");
        Candidate c = require(id);
        if (req.name() != null) c.setName(req.name());
        if (req.town() != null) c.setTown(req.town());
        if (req.dbCategory() != null) c.setDbCategory(req.dbCategory());
        if (req.turnoverMonthly() != null) c.setTurnoverMonthly(req.turnoverMonthly());
        if (req.expectedRcplTurnover() != null) c.setExpectedRcplTurnover(req.expectedRcplTurnover());
        if (req.coverageOutlets() != null) c.setCoverageOutlets(req.coverageOutlets());
        if (req.infraScore() != null) c.setInfraScore(req.infraScore());
        if (req.finEvalPct() != null) c.setFinEvalPct(req.finEvalPct());
        if (req.stage() != null) c.setStage(req.stage());
        if (req.confidencePct() != null) c.setConfidencePct(req.confidencePct());
        if (req.subtype() != null) c.setSubtype(req.subtype());
        if (req.oldDbCode() != null) c.setOldDbCode(req.oldDbCode());
        if (req.oldDbName() != null) c.setOldDbName(req.oldDbName());
        if (req.additionalReason() != null) c.setAdditionalReason(req.additionalReason());
        return toDto(candidateRepository.save(c));
    }

    @Transactional
    public CandidateDto moveStage(String id, String stage) {
        if (stage == null || stage.isBlank()) throw new ApiException.BadRequest("stage is required");
        Candidate c = require(id);
        c.setStage(stage);
        return toDto(candidateRepository.save(c));
    }

    /** Move to 'active' AND create/upsert the real Partner record (mirrors activateCandidate). */
    @Transactional
    public CandidateDto activate(CurrentUser user, String id, ActivateRequest req) {
        if (user == null) throw new ApiException.BadRequest("Current user is required");
        if (req == null) throw new ApiException.BadRequest("Request body is required");
        Candidate c = require(id);
        c.setStage("active");
        candidateRepository.save(c);
        partnerService.upsertActivePartner(id, req.partnerName(), req.partnerType(), req.state(), req.town());
        auditService.logHuman(user.name(), "Activated partner", req.partnerName());
        return toDto(c);
    }

    /** Toggle the cross-persona comparison shortlist flag. */
    @Transactional
    public CandidateDto setShortlisted(String id, boolean shortlisted) {
        Candidate c = require(id);
        c.setShortlisted(shortlisted);
        return toDto(candidateRepository.save(c));
    }

    @Transactional
    public CandidateDto reject(String id) {
        Candidate c = require(id);
        c.setStage("rejected");
        return toDto(candidateRepository.save(c));
    }

    @Transactional
    public CandidateDto reinstate(String id) {
        Candidate c = require(id);
        c.setStage("open");
        return toDto(candidateRepository.save(c));
    }

    /** Pull a lead into evaluation (deterministic scoring lives in the New Application flow). */
    @Transactional
    public CandidateDto evaluate(String id) {
        Candidate c = require(id);
        if ("open".equals(c.getStage())) c.setStage("pending");
        return toDto(candidateRepository.save(c));
    }

    @Transactional
    public CandidateDto setDiscontinuationForm(String id, JsonNode form) {
        Candidate c = require(id);
        c.setDiscontinuationForm(json.write(form));
        return toDto(candidateRepository.save(c));
    }

    private Candidate require(String id) {
        if (id == null || id.isBlank()) throw new ApiException.BadRequest("Candidate id is required");
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ApiException.NotFound("Candidate not found: " + id));
    }

    private CandidateDto toDto(Candidate c) {
        return new CandidateDto(
                c.getId(), c.getName(), c.getTown(), c.getDbCategory(), c.getTurnoverMonthly(),
                c.getExpectedRcplTurnover(), c.getCoverageOutlets(), c.getInfraScore(), c.getFinEvalPct(),
                c.getStage(), c.getConfidencePct(), c.isBestMatch(), c.isShortlisted(), c.isUserCreated(), c.getCreatedBy(),
                c.getCreatedAt(), c.getSourceIntakeId(), c.getSubtype(), c.getOldDbCode(), c.getOldDbName(),
                c.getAdditionalReason(), json.parse(c.getDiscontinuationForm()));
    }
}
