package com.rcpl.platform.template;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for the template engine (partner types). */
public final class PartnerTypeDtos {

    private PartnerTypeDtos() {}

    /** Mirrors PartnerType in the frontend contract. */
    public record PartnerTypeDto(
            String code, String label, boolean isActive, List<String> documents, List<String> workflow) {
    }

    public record UpsertPartnerTypeRequest(
            @NotBlank String label,
            Boolean isActive,
            Integer sortOrder,
            List<String> documents,
            List<String> workflow) {
    }
}
