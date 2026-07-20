package com.rcpl.platform.grievance.repository;

import java.util.List;

import com.rcpl.platform.grievance.entity.GrievanceUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrievanceUpdateRepository extends JpaRepository<GrievanceUpdate, Long> {
    List<GrievanceUpdate> findByGrievanceIdOrderByIdAsc(String grievanceId);
}
