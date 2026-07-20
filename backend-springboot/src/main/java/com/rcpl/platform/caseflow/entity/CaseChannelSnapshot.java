package com.rcpl.platform.caseflow.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Channel/infra readiness numbers behind a flag (shared PK with the case). */
@Entity
@Table(name = "case_channel_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class CaseChannelSnapshot {

    @Id
    @Column(name = "case_id")
    private Long caseId;

    @Column
    private BigDecimal score;

    @Column
    private BigDecimal threshold;

    @Column
    private BigDecimal gap;

    @Column(name = "readiness_pct")
    private BigDecimal readinessPct;
}
