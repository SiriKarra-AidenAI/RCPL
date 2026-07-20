package com.rcpl.platform.template;

import java.util.List;

import com.rcpl.platform.template.entity.PartnerTypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerTypeDocumentRepository extends JpaRepository<PartnerTypeDocument, Long> {
    List<PartnerTypeDocument> findByPartnerTypeCodeOrderBySortOrderAsc(String partnerTypeCode);
    void deleteByPartnerTypeCode(String partnerTypeCode);
}
