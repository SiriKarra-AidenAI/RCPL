package com.rcpl.platform.caseflow.repository;

import java.util.List;

import com.rcpl.platform.caseflow.entity.CaseLeadershipNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseLeadershipNoteRepository extends JpaRepository<CaseLeadershipNote, Long> {
    List<CaseLeadershipNote> findByCaseIdOrderByCreatedAtAsc(Long caseId);
}
