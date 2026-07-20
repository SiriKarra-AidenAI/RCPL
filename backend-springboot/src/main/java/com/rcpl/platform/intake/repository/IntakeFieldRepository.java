package com.rcpl.platform.intake.repository;

import java.util.List;

import com.rcpl.platform.intake.entity.IntakeField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeFieldRepository extends JpaRepository<IntakeField, Long> {
    List<IntakeField> findByIntakeId(String intakeId);
}
