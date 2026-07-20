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

/** An area within a GTM city. */
@Entity
@Table(name = "gtm_area")
@Getter
@Setter
@NoArgsConstructor
public class GtmArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    @Column(nullable = false, length = 128)
    private String name;

    @Column
    private Integer target = 0;

    @Column
    private Integer actual = 0;
}
