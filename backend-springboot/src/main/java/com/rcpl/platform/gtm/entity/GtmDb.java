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

/** A distributor listed under a GTM area. */
@Entity
@Table(name = "gtm_db")
@Getter
@Setter
@NoArgsConstructor
public class GtmDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_id", nullable = false)
    private Long areaId;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(name = "db_type", length = 64)
    private String dbType;

    @Column(length = 24)
    private String status; // Active | In review
}
