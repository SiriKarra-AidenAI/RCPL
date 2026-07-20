package com.rcpl.platform.audit;

import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for the audit log. */
public final class AuditDtos {

    private AuditDtos() {}

    public record AuditDto(String id, String when, String actor, String kind, String action, String entity) {
        public static AuditDto from(AuditEntry e) {
            return new AuditDto(e.getId(), e.getWhenLabel(), e.getActor(), e.getKind(), e.getAction(), e.getEntity());
        }
    }

    public record AppendAuditRequest(
            @NotBlank String actor, String kind, @NotBlank String action, @NotBlank String entity) {
    }
}
