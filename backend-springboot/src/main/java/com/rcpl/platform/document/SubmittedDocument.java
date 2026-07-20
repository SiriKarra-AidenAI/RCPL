package com.rcpl.platform.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A submitted document and its verification status (SubmittedDocument in the contract). */
@Entity
@Table(name = "submitted_documents")
@Getter
@Setter
@NoArgsConstructor
public class SubmittedDocument {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "case_code", length = 32)
    private String caseCode;

    @Column(name = "partner_type", length = 32)
    private String partnerType;

    @Column(name = "doc_name", nullable = false, length = 300)
    private String docName;

    @Column(length = 1000)
    private String claimed;

    @Column(length = 1000)
    private String extracted;

    @Column(nullable = false, length = 24)
    private String status; // not_checked | pending | verified | mismatch

    @Column(name = "file_name", length = 300)
    private String fileName;

    @Column(name = "uploaded_on", length = 32)
    private String uploadedOn;

    @Column(name = "uploaded_at", length = 32)
    private String uploadedAt;

    @Column(name = "verified_on", length = 32)
    private String verifiedOn;

    @Column
    private boolean optional;

    @Column(name = "this_week")
    private boolean thisWeek;
}
