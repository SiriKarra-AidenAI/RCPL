package com.rcpl.platform.gtm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A city within a GTM state. */
@Entity
@Table(name = "gtm_city")
@Getter
@Setter
@NoArgsConstructor
public class GtmCity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state_code", nullable = false, length = 8)
    private String stateCode;

    @Column(nullable = false, length = 128)
    private String name;

    @Column
    private Integer target = 0;

    @Column
    private Integer actual = 0;
}
