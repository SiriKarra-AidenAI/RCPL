package com.rcpl.platform.document;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmittedDocumentRepository extends JpaRepository<SubmittedDocument, String> {
    List<SubmittedDocument> findByCaseCode(String caseCode);
}
