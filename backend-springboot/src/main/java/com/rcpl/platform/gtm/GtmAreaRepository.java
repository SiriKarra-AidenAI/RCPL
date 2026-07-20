package com.rcpl.platform.gtm;

import java.util.List;

import com.rcpl.platform.gtm.entity.GtmArea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GtmAreaRepository extends JpaRepository<GtmArea, Long> {
    List<GtmArea> findByCityIdOrderByNameAsc(Long cityId);
}
