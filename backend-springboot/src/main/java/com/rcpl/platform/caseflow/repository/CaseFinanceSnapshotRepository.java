package com.rcpl.platform.caseflow.repository;

import com.rcpl.platform.caseflow.entity.CaseFinanceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseFinanceSnapshotRepository extends JpaRepository<CaseFinanceSnapshot, Long> {
}
