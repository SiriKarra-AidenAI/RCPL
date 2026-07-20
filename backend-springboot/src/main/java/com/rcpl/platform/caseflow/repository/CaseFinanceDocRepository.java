package com.rcpl.platform.caseflow.repository;

import java.util.List;
import java.util.Optional;

import com.rcpl.platform.caseflow.entity.CaseFinanceDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseFinanceDocRepository extends JpaRepository<CaseFinanceDoc, Long> {
    List<CaseFinanceDoc> findByCaseId(Long caseId);
    Optional<CaseFinanceDoc> findByCaseIdAndDocKey(Long caseId, String docKey);
}
