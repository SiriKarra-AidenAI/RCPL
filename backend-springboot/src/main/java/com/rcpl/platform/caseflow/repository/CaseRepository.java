package com.rcpl.platform.caseflow.repository;

import java.util.List;
import java.util.Optional;

import com.rcpl.platform.caseflow.entity.CaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CaseRepository extends JpaRepository<CaseEntity, Long>, JpaSpecificationExecutor<CaseEntity> {
    Optional<CaseEntity> findByCode(String code);
    boolean existsByCode(String code);
    List<CaseEntity> findByCandidateId(String candidateId);

    /** Highest existing numeric suffix for a case-code prefix, to sequence the next code. */
    List<CaseEntity> findByCodeStartingWith(String prefix);
}
