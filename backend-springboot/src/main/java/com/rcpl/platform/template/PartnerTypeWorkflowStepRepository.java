package com.rcpl.platform.template;

import java.util.List;

import com.rcpl.platform.template.entity.PartnerTypeWorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerTypeWorkflowStepRepository extends JpaRepository<PartnerTypeWorkflowStep, Long> {
    List<PartnerTypeWorkflowStep> findByPartnerTypeCodeOrderBySortOrderAsc(String partnerTypeCode);
    void deleteByPartnerTypeCode(String partnerTypeCode);
}
