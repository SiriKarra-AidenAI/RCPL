package com.rcpl.platform.intake.repository;

import java.util.List;

import com.rcpl.platform.intake.entity.IntakeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeItemRepository extends JpaRepository<IntakeItem, String> {
    List<IntakeItem> findByProcessedFalse();
}
