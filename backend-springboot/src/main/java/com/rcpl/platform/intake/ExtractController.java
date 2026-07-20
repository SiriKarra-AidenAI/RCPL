package com.rcpl.platform.intake;

import java.nio.charset.StandardCharsets;

import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.intake.IntakeDtos.ExtractDocumentResult;
import com.rcpl.platform.intake.IntakeDtos.ExtractRequest;
import com.rcpl.platform.intake.IntakeDtos.ExtractResultDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Deterministic (no AI/LLM) field extraction for the Intake Inbox's "Paste Email" flow and Intake
 * Review's document-upload flow — separate paths from /api/intake, same no-class-prefix style as
 * {@link InboxStatusController}. Ports RCPL-Angular-Frontend/src/app/lib/extract.ts's
 * extractEmail() via {@link FrontendFieldExtractor}; NOT the same extraction/taxonomy as
 * {@link IntakeParser}'s /api/intake/parse and /api/intake/ingest.
 */
@RestController
public class ExtractController {

    private final FrontendFieldExtractor extractor;

    public ExtractController(FrontendFieldExtractor extractor) {
        this.extractor = extractor;
    }

    @PostMapping("/api/extract")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public ExtractResultDto extract(@RequestBody ExtractRequest req) {
        return extractor.extract(req.source(), req.subject(), req.body());
    }

    /** Multipart document upload (field name "file") — best-effort, never errors on a bad/empty file. */
    @PostMapping("/api/extract-document")
    @RequireScreen(value = "/intake-inbox", manage = true)
    public ExtractDocumentResult extractDocument(@RequestParam("file") MultipartFile file) {
        String text = readText(file);
        return new ExtractDocumentResult(extractor.extract("", "", text).fields());
    }

    /**
     * Raw text of an uploaded file — PDFBox for {@code application/pdf}, straight UTF-8 decoding
     * otherwise (csv/txt/etc.). Any failure (corrupt/unsupported/empty file) yields "" rather than
     * propagating, so the caller always gets a (possibly all-unfound) field list instead of a 500 —
     * this is a best-effort deterministic feature, not a hard requirement.
     */
    private String readText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        try {
            byte[] bytes = file.getBytes();
            if ("application/pdf".equals(file.getContentType())) {
                try (PDDocument doc = Loader.loadPDF(bytes)) {
                    return new PDFTextStripper().getText(doc);
                }
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
