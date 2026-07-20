package com.rcpl.platform.partner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A partner directory record (distributor/vendor/etc). */
@Entity
@Table(name = "partners")
@Getter
@Setter
@NoArgsConstructor
public class Partner {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "legal_name", nullable = false, length = 300)
    private String legalName;

    @Column(name = "partner_type", nullable = false, length = 32)
    private String partnerType;

    @Column(length = 64)
    private String state;

    @Column(length = 128)
    private String town;

    @Column(nullable = false, length = 24)
    private String status; // in_review | active | discontinued

    @Column(name = "onboarded_at", length = 32)
    private String onboardedAt;

    @Column(name = "discontinued_at", length = 32)
    private String discontinuedAt;

    @Column(name = "db_code", length = 32)
    private String dbCode;

    @Column(name = "candidate_id", length = 64)
    private String candidateId;
}
