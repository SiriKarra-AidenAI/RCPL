package com.rcpl.platform.grievance.repository;

import com.rcpl.platform.grievance.entity.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrievanceRepository extends JpaRepository<Grievance, String> {
}
