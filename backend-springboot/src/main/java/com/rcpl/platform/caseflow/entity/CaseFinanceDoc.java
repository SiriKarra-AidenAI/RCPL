package com.rcpl.platform.caseflow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A financial-verification document attached to a case (keyed by doc_key). */
@Entity
@Table(name = "case_finance_docs")
@Getter
@Setter
@NoArgsConstructor
public class CaseFinanceDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "doc_key", nullable = false, length = 64)
    private String docKey;

    @Column(name = "file_name", length = 300)
    private String fileName;

    @Lob
    @Column(name = "data_url")
    private String dataUrl;
}
