package com.rcpl.platform.caseflow.service;

import com.rcpl.platform.caseflow.entity.CaseEntity;
import com.rcpl.platform.caseflow.repository.CaseRepository;
import org.springframework.stereotype.Service;

/**
 * Sequences the next case code (CMP-#### for most partner types, VND-#### for vendors),
 * mirroring nextCaseCode() in the frontend store.
 */
@Service
public class CaseCodeService {

    private final CaseRepository caseRepository;

    public CaseCodeService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public String next(String partnerType) {
        boolean vendor = "vendor".equals(partnerType);
        String prefix = vendor ? "VND" : "CMP";
        int base = vendor ? 417 : 2291;
        int max = base;
        for (CaseEntity c : caseRepository.findByCodeStartingWith(prefix + "-")) {
            try {
                int n = Integer.parseInt(c.getCode().substring(prefix.length() + 1));
                if (n > max) max = n;
            } catch (NumberFormatException ignored) {
                // non-numeric suffix — skip
            }
        }
        return String.format("%s-%04d", prefix, max + 1);
    }
}
