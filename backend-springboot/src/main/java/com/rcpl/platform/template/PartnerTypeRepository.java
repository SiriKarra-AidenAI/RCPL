package com.rcpl.platform.template;

import java.util.List;

import com.rcpl.platform.template.entity.PartnerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerTypeRepository extends JpaRepository<PartnerType, String> {
    List<PartnerType> findAllByOrderBySortOrderAsc();
}
