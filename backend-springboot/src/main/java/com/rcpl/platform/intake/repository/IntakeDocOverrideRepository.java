package com.rcpl.platform.intake.repository;

import java.util.List;
import java.util.Optional;

import com.rcpl.platform.intake.entity.IntakeDocOverride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntakeDocOverrideRepository extends JpaRepository<IntakeDocOverride, Long> {
    List<IntakeDocOverride> findByIntakeId(String intakeId);
    Optional<IntakeDocOverride> findByIntakeIdAndDocName(String intakeId, String docName);
    Optional<IntakeDocOverride> findByIntakeIdAndFileName(String intakeId, String fileName);
}
