package com.rcpl.platform.gtm;

import java.util.List;

import com.rcpl.platform.gtm.entity.GtmFactor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GtmFactorRepository extends JpaRepository<GtmFactor, String> {
    List<GtmFactor> findAllByOrderBySortOrderAsc();
}
