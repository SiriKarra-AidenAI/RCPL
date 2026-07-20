package com.rcpl.platform.caseflow.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Finance readiness numbers behind a flag (shared PK with the case). */
@Entity
@Table(name = "case_finance_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class CaseFinanceSnapshot {

    @Id
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "own_funds")
    private BigDecimal ownFunds;

    @Column(name = "cc_limit")
    private BigDecimal ccLimit;

    @Column(name = "capital_available")
    private BigDecimal capitalAvailable;

    @Column(name = "required_investment")
    private BigDecimal requiredInvestment;

    @Column(name = "funding_gap")
    private BigDecimal fundingGap;

    @Column(name = "readiness_pct")
    private BigDecimal readinessPct;
}
