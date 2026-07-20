package com.rcpl.platform.document;

import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for submitted documents. */
public final class DocumentDtos {

    private DocumentDtos() {}

    public record DocumentDto(
            String id,
            String caseCode,
            String partnerType,
            String docName,
            String claimed,
            String extracted,
            String status,
            String fileName,
            String uploadedOn,
            String uploadedAt,
            String verifiedOn,
            boolean optional,
            boolean thisWeek) {

        public static DocumentDto from(SubmittedDocument d) {
            return new DocumentDto(d.getId(), d.getCaseCode(), d.getPartnerType(), d.getDocName(),
                    d.getClaimed(), d.getExtracted(), d.getStatus(), d.getFileName(), d.getUploadedOn(),
                    d.getUploadedAt(), d.getVerifiedOn(), d.isOptional(), d.isThisWeek());
        }
    }

    public record CreateDocumentRequest(
            String caseCode,
            String partnerType,
            @NotBlank String docName,
            String claimed,
            String extracted,
            String fileName,
            boolean optional) {
    }
}
