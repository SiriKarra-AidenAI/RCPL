package com.rcpl.platform.gtm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** GTM coverage at state level (top of the state → city → area → DB hierarchy). */
@Entity
@Table(name = "gtm_state")
@Getter
@Setter
@NoArgsConstructor
public class GtmState {

    @Id
    @Column(length = 8)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 24)
    private String region;

    @Column
    private Integer target = 0;

    @Column
    private Integer actual = 0;
}
