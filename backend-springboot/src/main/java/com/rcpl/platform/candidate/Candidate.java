package com.rcpl.platform.candidate;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A lead in the candidate pipeline (CandidateCard in the frontend contract). */
@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
public class Candidate {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(length = 128)
    private String town;

    @Column(name = "db_category", length = 128)
    private String dbCategory;

    @Column(name = "turnover_monthly")
    private BigDecimal turnoverMonthly;

    @Column(name = "expected_rcpl_turnover")
    private BigDecimal expectedRcplTurnover;

    @Column(name = "coverage_outlets")
    private Integer coverageOutlets;

    @Column(name = "infra_score")
    private BigDecimal infraScore;

    @Column(name = "fin_eval_pct")
    private BigDecimal finEvalPct;

    @Column(nullable = false, length = 24)
    private String stage; // open|pending|approval_1|approval_2|active|rejected

    @Column(name = "confidence_pct")
    private BigDecimal confidencePct;

    @Column(name = "is_best_match")
    private boolean bestMatch;

    /** On the cross-persona comparison shortlist (Leads → New Application). */
    @Column(name = "shortlisted", nullable = false)
    private boolean shortlisted;

    @Column(name = "user_created")
    private boolean userCreated;

    @Column(name = "created_by", length = 32)
    private String createdBy;

    @Column(name = "created_at")
    private Long createdAt; // epoch ms

    @Column(name = "source_intake_id", length = 64)
    private String sourceIntakeId;

    @Column(length = 24)
    private String subtype; // new | replacement | additional

    @Column(name = "old_db_code", length = 32)
    private String oldDbCode;

    @Column(name = "old_db_name", length = 300)
    private String oldDbName;

    @Column(name = "additional_reason", length = 1000)
    private String additionalReason;

    /** DisengagementForm serialized as JSON text (null until filled in). */
    @Lob
    @Column(name = "discontinuation_form")
    private String discontinuationForm;
}
