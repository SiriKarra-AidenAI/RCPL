package com.rcpl.platform.config.onboarding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A DB category (the workbook's DB-type taxonomy). */
@Entity
@Table(name = "db_category")
@Getter
@Setter
@NoArgsConstructor
public class DbCategory {

    @Id
    @Column(length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
