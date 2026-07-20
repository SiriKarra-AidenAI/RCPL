package com.rcpl.platform.auth.repository;

import java.util.List;
import java.util.Optional;

import com.rcpl.platform.auth.entity.RoleDataScope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDataScopeRepository extends JpaRepository<RoleDataScope, RoleDataScope.Key> {
    List<RoleDataScope> findByRoleCode(String roleCode);
    Optional<RoleDataScope> findByRoleCodeAndEntity(String roleCode, String entity);
}
