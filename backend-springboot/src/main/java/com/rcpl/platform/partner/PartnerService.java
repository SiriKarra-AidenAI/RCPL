package com.rcpl.platform.partner;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.DataScopeService;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.common.DateLabels;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Partner directory reads (data-scoped) and the shared upsert-active-partner transition. */
@Service
public class PartnerService {

    private static final String ENTITY = "partners";

    private final PartnerRepository partnerRepository;
    private final DataScopeService dataScopeService;

    public PartnerService(PartnerRepository partnerRepository, DataScopeService dataScopeService) {
        this.partnerRepository = partnerRepository;
        this.dataScopeService = dataScopeService;
    }

    @Transactional(readOnly = true)
    public List<PartnerDto> list(CurrentUser user) {
        Specification<Partner> scope = dataScopeService.byState(user, ENTITY, "state");
        return partnerRepository.findAll(scope).stream().map(PartnerDto::from).toList();
    }

    @Transactional(readOnly = true)
    public PartnerDto get(CurrentUser user, String id) {
        Partner p = partnerRepository.findById(id)
                .orElseThrow(() -> new ApiException.NotFound("Partner not found: " + id));
        if (!dataScopeService.isStateVisible(user, ENTITY, p.getState())) {
            throw new ApiException.NotFound("Partner not found: " + id);
        }
        return PartnerDto.from(p);
    }

    /**
     * Promote a candidate to an active Partner, keyed by candidate id so it never collides with
     * an unrelated seed partner of the same name. Mirrors upsertActivePartner() in the store.
     */
    @Transactional
    public Partner upsertActivePartner(String candidateId, String partnerName, String partnerType,
                                       String state, String town) {
        String id = "candidate:" + candidateId;
        Partner existing = partnerRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setStatus("active");
            return partnerRepository.save(existing);
        }
        Partner p = new Partner();
        p.setId(id);
        p.setLegalName(partnerName);
        p.setPartnerType(partnerType);
        p.setState(state);
        p.setTown(town);
        p.setStatus("active");
        p.setOnboardedAt(DateLabels.dateStamp());
        p.setCandidateId(candidateId);
        if ("distributor".equals(partnerType)) {
            p.setDbCode(nextDbCode());
        }
        return partnerRepository.save(p);
    }

    /** Next DB-#### code, one past the highest in the directory (base 1000). */
    private String nextDbCode() {
        int max = 1000;
        for (Partner p : partnerRepository.findByPartnerType("distributor")) {
            String code = p.getDbCode();
            if (code != null && code.startsWith("DB-")) {
                try {
                    int n = Integer.parseInt(code.substring(3));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {
                    // skip
                }
            }
        }
        return "DB-" + (max + 1);
    }
}
