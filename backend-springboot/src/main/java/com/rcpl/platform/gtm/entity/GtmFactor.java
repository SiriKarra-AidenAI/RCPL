package com.rcpl.platform.gtm.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A GTM factor definition (drives the factors-vs-plan comparison). */
@Entity
@Table(name = "gtm_factor")
@Getter
@Setter
@NoArgsConstructor
public class GtmFactor {

    @Id
    @Column(name = "factor_key", length = 64)
    private String factorKey;

    @Column(nullable = false, length = 200)
    private String label;

    @Column(length = 200)
    private String sub;

    @Column(length = 64)
    private String icon;

    @Column(name = "per_target")
    private BigDecimal perTarget;

    @Column
    private BigDecimal delta;

    @Column(name = "is_money")
    private boolean money;

    @Column(name = "is_extra")
    private boolean extra;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
