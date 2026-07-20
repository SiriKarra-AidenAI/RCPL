package com.rcpl.platform.config.onboarding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One of the 8 infrastructure factors scored per candidate. */
@Entity
@Table(name = "infra_item")
@Getter
@Setter
@NoArgsConstructor
public class InfraItem {

    @Id
    @Column(name = "item_key", length = 64)
    private String itemKey;

    @Column(nullable = false, length = 200)
    private String label;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
