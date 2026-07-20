package com.rcpl.platform.caseflow.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for cases and their workflow actions. */
public final class CaseDtos {

    private CaseDtos() {}

    public record FinanceSnapshotDto(
            BigDecimal ownFunds, BigDecimal ccLimit, BigDecimal capitalAvailable,
            BigDecimal requiredInvestment, BigDecimal fundingGap, BigDecimal readinessPct) {
    }

    public record ChannelSnapshotDto(
            BigDecimal score, BigDecimal threshold, BigDecimal gap, BigDecimal readinessPct) {
    }

    public record FinanceDocDto(String name, String dataUrl) {
    }

    public record LeadershipNoteDto(String author, String body, String when) {
    }

    /** Full case record returned to the client (mirrors CaseRecord in the contract). */
    public record CaseDto(
            String code,
            String partnerName,
            String partnerType,
            String town,
            String state,
            String subtype,
            String status,
            String ownerRole,
            List<String> involvedRoles,
            String slaLabel,
            boolean isOverdue,
            boolean hasDiscontinuationForm,
            JsonNode discontinuationForm,
            BigDecimal confidencePct,
            String candidateId,
            String flagDetail,
            String signoffAuthority,
            boolean onboardingNotified,
            FinanceSnapshotDto financeSnapshot,
            ChannelSnapshotDto channelSnapshot,
            Map<String, FinanceDocDto> financeDocsUploaded,
            String channelDocUploaded,
            List<LeadershipNoteDto> notesForLeadership) {
    }

    /** Raise a flagged case from the New Application wizard. */
    public record RaiseCaseRequest(
            @NotBlank String partnerName,
            @NotBlank String partnerType,
            String town,
            String state,
            String subtype,
            @NotBlank String ownerRole,
            String candidateId,
            String flagDetail,
            String signoffAuthority,
            BigDecimal confidencePct,
            int slaHours,
            boolean hasDiscontinuationForm,
            JsonNode discontinuationForm,
            FinanceSnapshotDto financeSnapshot,
            ChannelSnapshotDto channelSnapshot) {
    }

    public record DecisionRequest(@NotBlank String decision) { // approved | rejected
    }

    public record FinanceDocRequest(@NotBlank String key, @NotBlank String fileName, String dataUrl) {
    }

    public record ChannelDocRequest(@NotBlank String fileName) {
    }

    public record NoteRequest(@NotBlank String author, @NotBlank String body) {
    }

    public record DiscontinuationRequest(JsonNode form) {
    }

    /** Deterministic scoring request for the wizard. */
    public record ScoreRequest(
            BigDecimal ownFunds, BigDecimal ccLimit, BigDecimal requiredInvestment,
            BigDecimal infraScore, BigDecimal infraThreshold) {
    }

    public record ScoreResponse(FinanceSnapshotDto finance, ChannelSnapshotDto channel) {
    }
}
