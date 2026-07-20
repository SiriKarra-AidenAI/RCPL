package com.rcpl.platform.partner;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartnerRepository extends JpaRepository<Partner, String>, JpaSpecificationExecutor<Partner> {
    List<Partner> findByPartnerType(String partnerType);
}
