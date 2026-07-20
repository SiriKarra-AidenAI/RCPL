package com.rcpl.platform.intake.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An ingested intake email/document, parsed deterministically (engine = 'rules'). */
@Entity
@Table(name = "intake_items")
@Getter
@Setter
@NoArgsConstructor
public class IntakeItem {

    @Id
    @Column(length = 64)
    private String id;

    @Column(length = 64)
    private String source;

    @Column(length = 500)
    private String subject;

    @Column(name = "received_at", length = 64)
    private String receivedAt;

    @Column(name = "partner_type", length = 32)
    private String partnerType;

    @Column(length = 24)
    private String priority;

    @Column(length = 64)
    private String region;

    @Column(length = 2000)
    private String summary;

    @Column(name = "confidence_pct")
    private BigDecimal confidencePct;

    @Column(length = 32)
    private String engine;

    @Lob
    @Column(name = "raw_json")
    private String rawJson;

    @Column(nullable = false)
    private boolean processed;

    @Column(name = "candidate_id", length = 64)
    private String candidateId;
}
