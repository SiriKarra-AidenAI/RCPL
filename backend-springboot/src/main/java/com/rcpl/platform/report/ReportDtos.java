package com.rcpl.platform.report;

import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for reports. */
public final class ReportDtos {

    private ReportDtos() {}

    public record ReportDto(String id, String name, String date, String format) {
        public static ReportDto from(Report r) {
            return new ReportDto(r.getId(), r.getName(), r.getDateLabel(), r.getFormat());
        }
    }

    public record CreateReportRequest(@NotBlank String name, @NotBlank String format) {
    }
}
