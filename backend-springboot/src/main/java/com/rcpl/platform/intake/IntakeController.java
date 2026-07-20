package com.rcpl.platform.intake;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.candidate.CandidateDtos.CandidateDto;
import com.rcpl.platform.intake.IntakeDtos.CreateLeadRequest;
import com.rcpl.platform.intake.IntakeDtos.DocOverrideRequest;
import com.rcpl.platform.intake.IntakeDtos.IngestRequest;
import com.rcpl.platform.intake.IntakeDtos.IntakeItemDto;
import com.rcpl.platform.intake.IntakeDtos.LocateTextRequest;
import com.rcpl.platform.intake.IntakeDtos.LocateTextResult;
import com.rcpl.platform.intake.IntakeDtos.ParseRequest;
import com.rcpl.platform.intake.IntakeDtos.ParseResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Intake inbox API — deterministic parsing, ingestion, and lead conversion. */
@RestController
@RequestMapping("/api/intake")
public class IntakeController {

    private final IntakeService intakeService;

    public IntakeController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @GetMapping
    @RequireScreen("/intake-inbox")
    public List<IntakeItemDto> list() {
        return intakeService.listUnprocessed();
    }

    @PostMapping("/parse")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public ParseResult parse(@Valid @RequestBody ParseRequest req) {
        return intakeService.parse(req.subject(), req.body());
    }

    /** Manual ingestion (also used by the IMAP poller) — persists an intake item. */
    @PostMapping("/ingest")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public IntakeItemDto ingest(@Valid @RequestBody IngestRequest req) {
        return intakeService.ingest(req.source(), req.subject(), req.body());
    }

    @PostMapping("/{id}/create-lead")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public CandidateDto createLead(@AuthenticationPrincipal CurrentUser user, @PathVariable String id,
                                   @RequestBody(required = false) CreateLeadRequest overrides) {
        return intakeService.createLead(user, id, overrides);
    }

    @PostMapping("/{id}/process")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public IntakeItemDto process(@PathVariable String id) {
        return intakeService.process(id);
    }

    /** Replace/upload a document on an intake item (Intake Review). */
    @PostMapping("/{id}/doc-override")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public IntakeItemDto docOverride(@PathVariable String id, @Valid @RequestBody DocOverrideRequest req) {
        return intakeService.setDocOverride(id, req.docName(), req.fileName(), req.dataUrl());
    }

    /** Raw bytes of a document uploaded/replaced via Intake Review (document viewer "view full document"). */
    @GetMapping("/{id}/attachment")
    @RequireScreen("/intake-inbox")
    public ResponseEntity<byte[]> attachment(@PathVariable String id, @RequestParam String filename) {
        IntakeService.Attachment a = intakeService.attachment(id, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(a.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename.replace("\"", "") + "\"")
                .body(a.bytes());
    }

    /** Deterministic (no AI) on-page location of a query string, for the document viewer's highlight overlay. */
    @PostMapping("/{id}/locate-text")
    @RequireScreen("/intake-inbox")
    public LocateTextResult locateText(@PathVariable String id, @Valid @RequestBody LocateTextRequest req) {
        return intakeService.locateText(id, req.filename(), req.query());
    }
}
