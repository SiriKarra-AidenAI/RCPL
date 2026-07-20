package com.rcpl.platform.caseflow.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A case in the approvals workflow (CaseRecord in the frontend contract). */
@Entity
@Table(name = "cases")
@Getter
@Setter
@NoArgsConstructor
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String code; // CMP-#### / VND-####

    @Column(name = "partner_name", nullable = false, length = 300)
    private String partnerName;

    @Column(name = "partner_type", nullable = false, length = 32)
    private String partnerType;

    @Column(length = 128)
    private String town;

    @Column(length = 64)
    private String state;

    @Column(nullable = false, length = 24)
    private String subtype; // new | replacement | additional

    @Column(nullable = false, length = 24)
    private String status; // draft|auto_cleared|flagged|approved|rejected

    @Column(name = "owner_role", nullable = false, length = 32)
    private String ownerRole;

    @Column(name = "signoff_authority", length = 8)
    private String signoffAuthority; // SM | RBL

    @Column(name = "sla_label", length = 32)
    private String slaLabel;

    @Column(name = "is_overdue", nullable = false)
    private boolean overdue;

    @Column(name = "has_discontinuation_form", nullable = false)
    private boolean hasDiscontinuationForm;

    @Lob
    @Column(name = "discontinuation_form")
    private String discontinuationForm; // DisengagementForm as JSON text

    @Column(name = "confidence_pct")
    private BigDecimal confidencePct;

    @Column(name = "flag_detail", length = 2000)
    private String flagDetail;

    @Column(name = "candidate_id", length = 64)
    private String candidateId;

    @Column(name = "onboarding_notified", nullable = false)
    private boolean onboardingNotified;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    /** Every role that has ever owned this case, including the current owner. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "case_involved_roles", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "role_code", length = 32)
    private Set<String> involvedRoles = new LinkedHashSet<>();
}
