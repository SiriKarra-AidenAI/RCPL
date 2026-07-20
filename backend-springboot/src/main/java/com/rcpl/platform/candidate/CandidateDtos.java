package com.rcpl.platform.candidate;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for the candidate (lead) pipeline. */
public final class CandidateDtos {

    private CandidateDtos() {}

    public record CandidateDto(
            String id,
            String name,
            String town,
            String dbCategory,
            BigDecimal turnoverMonthly,
            BigDecimal expectedRcplTurnover,
            Integer coverageOutlets,
            BigDecimal infraScore,
            BigDecimal finEvalPct,
            String stage,
            BigDecimal confidencePct,
            boolean isBestMatch,
            boolean shortlisted,
            boolean userCreated,
            String createdBy,
            Long createdAt,
            String sourceIntakeId,
            String subtype,
            String oldDbCode,
            String oldDbName,
            String additionalReason,
            JsonNode discontinuationForm) {
    }

    public record CreateCandidateRequest(
            @NotBlank String name,
            String town,
            String dbCategory,
            BigDecimal turnoverMonthly,
            BigDecimal expectedRcplTurnover,
            Integer coverageOutlets,
            BigDecimal infraScore,
            BigDecimal finEvalPct,
            String stage,
            BigDecimal confidencePct,
            String sourceIntakeId,
            String subtype,
            String oldDbCode,
            String oldDbName,
            String additionalReason,
            JsonNode discontinuationForm) {
    }

    /** Partial update — non-null fields are applied. */
    public record UpdateCandidateRequest(
            String name,
            String town,
            String dbCategory,
            BigDecimal turnoverMonthly,
            BigDecimal expectedRcplTurnover,
            Integer coverageOutlets,
            BigDecimal infraScore,
            BigDecimal finEvalPct,
            String stage,
            BigDecimal confidencePct,
            String subtype,
            String oldDbCode,
            String oldDbName,
            String additionalReason) {
    }

    public record MoveStageRequest(@NotBlank String stage) {
    }

    public record ActivateRequest(
            @NotBlank String partnerName,
            @NotBlank String partnerType,
            String town,
            String state) {
    }

    public record DiscontinuationFormRequest(JsonNode form) {
    }
}
