package com.rcpl.platform.caseflow.repository;

import java.util.List;

import com.rcpl.platform.caseflow.entity.CaseChannelDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseChannelDocRepository extends JpaRepository<CaseChannelDoc, Long> {
    List<CaseChannelDoc> findByCaseId(Long caseId);
}
