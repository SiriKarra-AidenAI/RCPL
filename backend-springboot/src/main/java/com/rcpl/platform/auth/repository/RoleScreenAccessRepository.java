package com.rcpl.platform.auth.repository;

import java.util.List;

import com.rcpl.platform.auth.entity.RoleScreenAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleScreenAccessRepository extends JpaRepository<RoleScreenAccess, RoleScreenAccess.Key> {
    List<RoleScreenAccess> findByRoleCode(String roleCode);
}
