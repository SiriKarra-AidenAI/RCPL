package com.rcpl.platform.caseflow.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.rcpl.platform.caseflow.dto.CaseDtos.ChannelSnapshotDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.FinanceSnapshotDto;
import org.springframework.stereotype.Service;

/**
 * Pure, deterministic recommendation/evaluation scoring — no model, no randomness. Ports the
 * finance/channel readiness math from the frontend so the same inputs always yield the same output.
 */
@Service
public class ScoringService {

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public FinanceSnapshotDto financeSnapshot(BigDecimal ownFunds, BigDecimal ccLimit, BigDecimal requiredInvestment) {
        BigDecimal own = nz(ownFunds);
        BigDecimal cc = nz(ccLimit);
        BigDecimal required = nz(requiredInvestment);
        BigDecimal available = own.add(cc);
        BigDecimal gap = required.subtract(available).max(BigDecimal.ZERO);
        BigDecimal readiness = required.signum() == 0
                ? BigDecimal.valueOf(100)
                : available.multiply(BigDecimal.valueOf(100)).divide(required, 0, RoundingMode.HALF_UP);
        return new FinanceSnapshotDto(own, cc, available, required, gap, readiness);
    }

    public ChannelSnapshotDto channelSnapshot(BigDecimal score, BigDecimal threshold) {
        BigDecimal s = nz(score);
        BigDecimal t = nz(threshold);
        BigDecimal gap = t.subtract(s).max(BigDecimal.ZERO);
        BigDecimal readiness = t.signum() == 0
                ? BigDecimal.valueOf(100)
                : s.multiply(BigDecimal.valueOf(100)).divide(t, 0, RoundingMode.HALF_UP);
        return new ChannelSnapshotDto(s, t, gap, readiness);
    }
}
