package com.rcpl.platform.gtm;

import java.util.List;

import com.rcpl.platform.gtm.entity.GtmCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GtmCityRepository extends JpaRepository<GtmCity, Long> {
    List<GtmCity> findByStateCodeOrderByNameAsc(String stateCode);
    void deleteByStateCode(String stateCode);
}
