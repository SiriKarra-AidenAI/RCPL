package com.rcpl.platform.template;

import java.util.List;

import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.template.PartnerTypeDtos.PartnerTypeDto;
import com.rcpl.platform.template.PartnerTypeDtos.UpsertPartnerTypeRequest;
import com.rcpl.platform.template.entity.PartnerType;
import com.rcpl.platform.template.entity.PartnerTypeDocument;
import com.rcpl.platform.template.entity.PartnerTypeWorkflowStep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Template engine — partner-type config (documents + approval workflow). */
@Service
public class PartnerTypeService {

    private final PartnerTypeRepository typeRepo;
    private final PartnerTypeDocumentRepository docRepo;
    private final PartnerTypeWorkflowStepRepository stepRepo;

    public PartnerTypeService(PartnerTypeRepository typeRepo, PartnerTypeDocumentRepository docRepo,
                              PartnerTypeWorkflowStepRepository stepRepo) {
        this.typeRepo = typeRepo;
        this.docRepo = docRepo;
        this.stepRepo = stepRepo;
    }

    @Transactional(readOnly = true)
    public List<PartnerTypeDto> list() {
        return typeRepo.findAllByOrderBySortOrderAsc().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public PartnerTypeDto get(String code) {
        return toDto(require(code));
    }

    @Transactional
    public PartnerTypeDto upsert(String code, UpsertPartnerTypeRequest req) {
        PartnerType t = typeRepo.findById(code).orElseGet(PartnerType::new);
        t.setCode(code);
        t.setLabel(req.label());
        t.setActive(req.isActive() == null || req.isActive());
        if (req.sortOrder() != null) t.setSortOrder(req.sortOrder());
        typeRepo.save(t);

        if (req.documents() != null) {
            docRepo.deleteByPartnerTypeCode(code);
            int i = 1;
            for (String name : req.documents()) {
                PartnerTypeDocument d = new PartnerTypeDocument();
                d.setPartnerTypeCode(code);
                d.setDocName(name);
                d.setSortOrder(i++);
                docRepo.save(d);
            }
        }
        if (req.workflow() != null) {
            stepRepo.deleteByPartnerTypeCode(code);
            int i = 1;
            for (String step : req.workflow()) {
                PartnerTypeWorkflowStep s = new PartnerTypeWorkflowStep();
                s.setPartnerTypeCode(code);
                s.setStep(step);
                s.setSortOrder(i++);
                stepRepo.save(s);
            }
        }
        return toDto(require(code));
    }

    @Transactional
    public void delete(String code) {
        require(code);
        docRepo.deleteByPartnerTypeCode(code);
        stepRepo.deleteByPartnerTypeCode(code);
        typeRepo.deleteById(code);
    }

    private PartnerType require(String code) {
        return typeRepo.findById(code)
                .orElseThrow(() -> new ApiException.NotFound("Partner type not found: " + code));
    }

    private PartnerTypeDto toDto(PartnerType t) {
        List<String> docs = docRepo.findByPartnerTypeCodeOrderBySortOrderAsc(t.getCode())
                .stream().map(PartnerTypeDocument::getDocName).toList();
        List<String> workflow = stepRepo.findByPartnerTypeCodeOrderBySortOrderAsc(t.getCode())
                .stream().map(PartnerTypeWorkflowStep::getStep).toList();
        return new PartnerTypeDto(t.getCode(), t.getLabel(), t.isActive(), docs, workflow);
    }
}
