package com.rcpl.platform.audit;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<AuditEntry, String> {
    List<AuditEntry> findAllByOrderByCreatedAtDesc();
    Page<AuditEntry> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
