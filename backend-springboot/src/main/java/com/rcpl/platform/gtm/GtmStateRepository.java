package com.rcpl.platform.gtm;

import java.util.List;

import com.rcpl.platform.gtm.entity.GtmState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GtmStateRepository extends JpaRepository<GtmState, String> {
    List<GtmState> findAllByOrderByNameAsc();
}
