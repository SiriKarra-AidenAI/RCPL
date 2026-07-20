package com.rcpl.platform.template.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One approval-workflow step for a partner type. */
@Entity
@Table(name = "partner_type_workflow")
@Getter
@Setter
@NoArgsConstructor
public class PartnerTypeWorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_type_code", nullable = false, length = 32)
    private String partnerTypeCode;

    @Column(nullable = false, length = 200)
    private String step;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
