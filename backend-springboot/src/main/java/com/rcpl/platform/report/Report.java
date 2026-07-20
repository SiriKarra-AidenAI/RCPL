package com.rcpl.platform.report;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A shareable Leadership report (ReportItem in the contract). */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(length = 24)
    private String format;

    @Column(name = "date_label", length = 32)
    private String dateLabel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
