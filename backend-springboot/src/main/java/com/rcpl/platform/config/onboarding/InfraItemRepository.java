package com.rcpl.platform.config.onboarding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InfraItemRepository extends JpaRepository<InfraItem, String> {
    List<InfraItem> findAllByOrderBySortOrderAsc();
}
