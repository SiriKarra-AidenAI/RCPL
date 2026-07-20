package com.rcpl.platform.gtm;

import java.util.List;

import com.rcpl.platform.gtm.entity.GtmDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GtmDbRepository extends JpaRepository<GtmDb, Long> {
    List<GtmDb> findByAreaIdOrderByNameAsc(Long areaId);
}
