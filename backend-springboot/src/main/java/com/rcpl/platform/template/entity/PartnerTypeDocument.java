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

/** A required document for a partner type. */
@Entity
@Table(name = "partner_type_documents")
@Getter
@Setter
@NoArgsConstructor
public class PartnerTypeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_type_code", nullable = false, length = 32)
    private String partnerTypeCode;

    @Column(name = "doc_name", nullable = false, length = 200)
    private String docName;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}
