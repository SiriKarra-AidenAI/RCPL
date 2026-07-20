package com.rcpl.platform.auth.repository;

import java.util.List;

import com.rcpl.platform.auth.entity.RoleAnalyticsSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleAnalyticsSectionRepository extends JpaRepository<RoleAnalyticsSection, RoleAnalyticsSection.Key> {
    List<RoleAnalyticsSection> findByRoleCode(String roleCode);
}
