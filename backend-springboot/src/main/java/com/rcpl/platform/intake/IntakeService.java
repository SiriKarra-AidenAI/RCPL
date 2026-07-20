package com.rcpl.platform.intake;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.rcpl.platform.audit.AuditService;
import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.candidate.CandidateDtos.CandidateDto;
import com.rcpl.platform.candidate.CandidateDtos.CreateCandidateRequest;
import com.rcpl.platform.candidate.CandidateService;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.DateLabels;
import com.rcpl.platform.common.Ids;
import com.rcpl.platform.common.JsonSupport;
import com.rcpl.platform.intake.IntakeDtos.CreateLeadRequest;
import com.rcpl.platform.intake.IntakeDtos.DocOverrideDto;
import com.rcpl.platform.intake.IntakeDtos.InboxStatusDto;
import com.rcpl.platform.intake.IntakeDtos.IntakeFieldDto;
import com.rcpl.platform.intake.IntakeDtos.IntakeItemDto;
import com.rcpl.platform.intake.IntakeDtos.LocateTextResult;
import com.rcpl.platform.intake.IntakeDtos.ParseResult;
import com.rcpl.platform.intake.entity.IntakeDocOverride;
import com.rcpl.platform.intake.entity.IntakeField;
import com.rcpl.platform.intake.entity.IntakeItem;
import com.rcpl.platform.intake.repository.IntakeDocOverrideRepository;
import com.rcpl.platform.intake.repository.IntakeFieldRepository;
import com.rcpl.platform.intake.repository.IntakeItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Email intake: ingest (from the poller or a manual post), deterministic parse, and lead conversion. */
@Service
public class IntakeService {

    private final IntakeItemRepository itemRepo;
    private final IntakeFieldRepository fieldRepo;
    private final IntakeDocOverrideRepository overrideRepo;
    private final IntakeParser parser;
    private final CandidateService candidateService;
    private final AuditService auditService;
    private final JsonSupport json;
    private final PdfTextLocator pdfTextLocator;
    private final boolean pollEnabled;
    private final String imapUsername;

    public IntakeService(IntakeItemRepository itemRepo, IntakeFieldRepository fieldRepo,
                         IntakeDocOverrideRepository overrideRepo, IntakeParser parser,
                         CandidateService candidateService, AuditService auditService, JsonSupport json,
                         PdfTextLocator pdfTextLocator,
                         @Value("${app.intake.poll-enabled:false}") boolean pollEnabled,
                         @Value("${spring.mail.username:}") String imapUsername) {
        this.itemRepo = itemRepo;
        this.fieldRepo = fieldRepo;
        this.overrideRepo = overrideRepo;
        this.parser = parser;
        this.candidateService = candidateService;
        this.auditService = auditService;
        this.json = json;
        this.pdfTextLocator = pdfTextLocator;
        this.pollEnabled = pollEnabled;
        this.imapUsername = imapUsername;
    }

    /** Best-effort mailbox status for the Intake Inbox banner — no live IMAP check, just config. */
    public InboxStatusDto inboxStatus() {
        boolean configured = pollEnabled && imapUsername != null && !imapUsername.isBlank();
        return new InboxStatusDto(configured, configured, configured ? imapUsername : null, null);
    }

    /** Decoded bytes + MIME type for a document uploaded/replaced via Intake Review. */
    public record Attachment(String contentType, byte[] bytes) {
    }

    /** The real bytes behind a doc-override's data: URL (Intake Review's "View full document"). */
    @Transactional(readOnly = true)
    public Attachment attachment(String id, String filename) {
        IntakeDocOverride o = overrideRepo.findByIntakeIdAndFileName(id, filename)
                .orElseThrow(() -> new ApiException.NotFound("No attachment named " + filename + " on intake " + id));
        String dataUrl = o.getDataUrl();
        if (dataUrl == null || !dataUrl.startsWith("data:")) {
            throw new ApiException.NotFound("Attachment " + filename + " has no stored bytes");
        }
        int comma = dataUrl.indexOf(',');
        String header = dataUrl.substring(5, comma); // "<mime>;base64"
        String mime = header.contains(";") ? header.substring(0, header.indexOf(';')) : header;
        byte[] bytes = java.util.Base64.getDecoder().decode(dataUrl.substring(comma + 1));
        return new Attachment(mime.isBlank() ? "application/octet-stream" : mime, bytes);
    }

    /** Deterministic (no AI) on-page location of a query string within a stored attachment. */
    @Transactional(readOnly = true)
    public LocateTextResult locateText(String id, String filename, String query) {
        Attachment a = attachment(id, filename);
        return new LocateTextResult(pdfTextLocator.locate(a.bytes(), query));
    }

    /** Upsert a replaced document for an intake item (Intake Review). */
    @Transactional
    public IntakeItemDto setDocOverride(String id, String docName, String fileName, String dataUrl) {
        require(id);
        IntakeDocOverride o = overrideRepo.findByIntakeIdAndDocName(id, docName)
                .orElseGet(IntakeDocOverride::new);
        o.setIntakeId(id);
        o.setDocName(docName);
        o.setFileName(fileName);
        o.setDataUrl(dataUrl);
        overrideRepo.save(o);
        return toDto(require(id));
    }

    @Transactional(readOnly = true)
    public List<IntakeItemDto> listUnprocessed() {
        return itemRepo.findByProcessedFalse().stream().map(this::toDto).toList();
    }

    /** Deterministic re-parse of raw text, without persisting anything. */
    public ParseResult parse(String subject, String body) {
        return parser.parse(subject, body);
    }

    /** Ingest an email into a persisted intake item + its parsed fields. */
    @Transactional
    public IntakeItemDto ingest(String source, String subject, String body) {
        ParseResult parsed = parser.parse(subject, body);
        IntakeItem item = new IntakeItem();
        item.setId(Ids.newId("intake"));
        item.setSource(source == null ? "email" : source);
        item.setSubject(subject);
        item.setReceivedAt(DateLabels.auditStamp());
        item.setPartnerType(parsed.partnerType());
        item.setPriority("normal");
        item.setSummary(parsed.summary());
        item.setConfidencePct(parsed.confidencePct());
        item.setEngine("rules");
        item.setRawJson(json.write(Map.of("subject", subject == null ? "" : subject, "body", body == null ? "" : body)));
        item.setProcessed(false);
        itemRepo.save(item);
        for (IntakeFieldDto f : parsed.fields()) {
            IntakeField fe = new IntakeField();
            fe.setIntakeId(item.getId());
            fe.setLabel(f.label());
            fe.setValue(f.value());
            fe.setOk(f.ok());
            fieldRepo.save(fe);
        }
        return toDto(item);
    }

    /** Convert an intake item into a candidate (lead) and mark it processed. */
    @Transactional
    public CandidateDto createLead(CurrentUser user, String id, CreateLeadRequest overrides) {
        IntakeItem item = require(id);
        List<IntakeField> fields = fieldRepo.findByIntakeId(id);
        String name = override(overrides == null ? null : overrides.name(), value(fields, "Distributor Name"), "New intake lead");
        String town = override(overrides == null ? null : overrides.town(), value(fields, "Town"), null);
        String dbCategory = override(overrides == null ? null : overrides.dbCategory(), value(fields, "DB Category"), null);

        CreateCandidateRequest req = new CreateCandidateRequest(
                name, town, dbCategory,
                number(value(fields, "Monthly Turnover")), null,
                integer(value(fields, "Coverage Outlets")), null, null,
                "open", item.getConfidencePct(), item.getId(),
                "new", null, null, null, null);

        CandidateDto candidate = candidateService.create(user, req);
        item.setProcessed(true);
        item.setCandidateId(candidate.id());
        itemRepo.save(item);
        auditService.logHuman(user.name(), "Created lead from intake", item.getSubject());
        return candidate;
    }

    @Transactional
    public IntakeItemDto process(String id) {
        IntakeItem item = require(id);
        item.setProcessed(true);
        return toDto(itemRepo.save(item));
    }

    private IntakeItem require(String id) {
        return itemRepo.findById(id)
                .orElseThrow(() -> new ApiException.NotFound("Intake item not found: " + id));
    }

    private IntakeItemDto toDto(IntakeItem item) {
        List<IntakeFieldDto> fields = fieldRepo.findByIntakeId(item.getId()).stream()
                .map(f -> new IntakeFieldDto(f.getLabel(), f.getValue(), f.isOk()))
                .toList();
        List<DocOverrideDto> overrides = overrideRepo.findByIntakeId(item.getId()).stream()
                .map(o -> new DocOverrideDto(o.getDocName(), o.getFileName()))
                .toList();
        return new IntakeItemDto(item.getId(), "email", item.getSource(), item.getSubject(), item.getReceivedAt(),
                item.getPartnerType(), item.getPriority(), item.getRegion(), item.getSummary(),
                item.getConfidencePct(), item.getEngine(), item.isProcessed(), item.getCandidateId(),
                fields, overrides);
    }

    private static String value(List<IntakeField> fields, String label) {
        return fields.stream().filter(f -> label.equals(f.getLabel())).map(IntakeField::getValue)
                .findFirst().orElse(null);
    }

    private static String override(String override, String parsed, String fallback) {
        if (override != null && !override.isBlank()) return override;
        if (parsed != null && !parsed.isBlank()) return parsed;
        return fallback;
    }

    private static BigDecimal number(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replaceAll("[^0-9.]", "");
        if (cleaned.isBlank()) return null;
        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer integer(String raw) {
        if (raw == null) return null;
        String cleaned = raw.replaceAll("[^0-9]", "");
        if (cleaned.isBlank()) return null;
        try {
            return Integer.valueOf(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
