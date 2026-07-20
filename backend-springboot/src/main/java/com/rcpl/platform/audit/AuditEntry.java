package com.rcpl.platform.audit;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An audit-log row (AuditEntry in the contract). */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
public class AuditEntry {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "when_label", length = 64)
    private String whenLabel;

    @Column(length = 200)
    private String actor;

    @Column(length = 24)
    private String kind; // human | system

    @Column(length = 1000)
    private String action;

    @Column(length = 200)
    private String entity;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
