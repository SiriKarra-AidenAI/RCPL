package com.rcpl.platform.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A configurable partner type (template engine): its documents + workflow live in child tables. */
@Entity
@Table(name = "partner_types")
@Getter
@Setter
@NoArgsConstructor
public class PartnerType {

    @Id
    @Column(length = 32)
    private String code;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
