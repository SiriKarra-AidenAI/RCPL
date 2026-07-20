package com.rcpl.platform.config.onboarding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DbCategoryRepository extends JpaRepository<DbCategory, String> {
    List<DbCategory> findAllByOrderBySortOrderAsc();
}
