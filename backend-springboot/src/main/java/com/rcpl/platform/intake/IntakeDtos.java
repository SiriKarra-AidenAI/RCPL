package com.rcpl.platform.intake;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for email intake. */
public final class IntakeDtos {

    private IntakeDtos() {}

    public record IntakeFieldDto(String label, String value, boolean ok) {
    }

    public record DocOverrideDto(String docName, String fileName) {
    }

    /**
     * Field names mirror the frontend's Extraction contract (RCPL-Angular-Frontend/src/app/mock/intake.ts)
     * — "channel" and "title", not "source-type"/"subject" — so the Angular inbox can merge a
     * server-returned item straight into its EXTRACTIONS map with no field remapping.
     */
    public record IntakeItemDto(
            String id, String channel, String source, String title, String receivedAt, String partnerType,
            String priority, String region, String summary, BigDecimal confidencePct, String engine,
            boolean processed, String candidateId, List<IntakeFieldDto> fields, List<DocOverrideDto> docOverrides) {
    }

    public record DocOverrideRequest(
            @NotBlank String docName, String fileName, String dataUrl) {
    }

    /** Deterministic parse result (transient — used by /parse before an item exists). */
    public record ParseResult(
            String partnerType, String summary, BigDecimal confidencePct, List<IntakeFieldDto> fields) {
    }

    public record ParseRequest(String subject, @NotBlank String body) {
    }

    public record IngestRequest(String source, @NotBlank String subject, @NotBlank String body) {
    }

    /** Optional overrides when converting an intake item into a candidate. */
    public record CreateLeadRequest(String name, String town, String dbCategory) {
    }

    /** IMAP mailbox connectivity, surfaced by the Intake Inbox banner. */
    public record InboxStatusDto(boolean connected, boolean configured, String address, String error) {
    }

    /** Request to find a query string's on-page position(s) in a stored attachment (document viewer). */
    public record LocateTextRequest(String filename, String query) {
    }

    /** One highlightable hit: 0-indexed page, and x/y/width/height as 0..1 fractions of page size. */
    public record PdfMatchDto(int page, double x, double y, double width, double height) {
    }

    public record LocateTextResult(List<PdfMatchDto> matches) {
    }

    // ---- /api/extract + /api/extract-document — FrontendFieldExtractor's output contract ----
    // Separate from ParseResult/ParseRequest/IngestRequest above: those back /api/intake/parse and
    // /api/intake/ingest (IntakeParser's own DB-persisted field taxonomy). These back the standalone
    // endpoints that port RCPL-Angular-Frontend/src/app/lib/extract.ts's extractEmail() shape
    // byte-for-byte — do not conflate the two.

    public record ExtractRequest(String source, String subject, String body) {
    }

    /** Mirrors extract.ts's `ExtractResult` shape exactly. */
    public record ExtractResultDto(
            List<IntakeFieldDto> fields, String summary, String partnerType, String priority, String region,
            BigDecimal confidencePct, int captured) {
    }

    /** /api/extract-document's response — the frontend caller only reads `.fields`. */
    public record ExtractDocumentResult(List<IntakeFieldDto> fields) {
    }
}
