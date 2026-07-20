package com.rcpl.platform.mail;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for outbound email (Intake Review, New Application, Approvals). */
public final class MailDtos {

    private MailDtos() {}

    /**
     * {@code itemId} + {@code attachDocs} are optional — only the Intake Review "request missing
     * info" reply sends them, to attach stored intake documents by their doc NAME (e.g. "GST
     * Certificate"), not filename.
     */
    public record MailReplyRequest(
            @NotBlank String to, String subject, @NotBlank String text, String itemId, List<String> attachDocs) {
    }

    public record MailReplyResult(boolean sent) {
    }
}
