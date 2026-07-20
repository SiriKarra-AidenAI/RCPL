package com.rcpl.platform.auth.repository;

import com.rcpl.platform.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {
}
