package com.rcpl.platform.grievance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A distributor grievance (Grievance in the contract). */
@Entity
@Table(name = "grievances")
@Getter
@Setter
@NoArgsConstructor
public class Grievance {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 300)
    private String distributor;

    @Column(length = 128)
    private String town;

    @Column(length = 24)
    private String channel; // Email | Phone | Portal | Field visit

    @Column(length = 128)
    private String category;

    @Column(length = 16)
    private String priority; // low | medium | high

    @Column(length = 300)
    private String subject;

    @Column(length = 2000)
    private String detail;

    @Column(name = "owner_role", length = 32)
    private String ownerRole;

    @Column(nullable = false, length = 24)
    private String status; // open | in_progress | resolved

    @Column(name = "sla_label", length = 32)
    private String slaLabel;

    @Column(name = "is_overdue", nullable = false)
    private boolean overdue;

    @Column(name = "raised_on", length = 32)
    private String raisedOn;
}
