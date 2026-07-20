package com.rcpl.platform.analytics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.rcpl.platform.candidate.Candidate;
import com.rcpl.platform.candidate.CandidateRepository;
import com.rcpl.platform.caseflow.entity.CaseEntity;
import com.rcpl.platform.caseflow.repository.CaseRepository;
import com.rcpl.platform.common.ApiException;
import com.rcpl.platform.partner.Partner;
import com.rcpl.platform.partner.PartnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Deterministic Analytics aggregates computed from live partner, candidate and case records. */
@Service
public class AnalyticsService {

    private static final Pattern YEAR = Pattern.compile("(\\d{4})");

    private final PartnerRepository partnerRepository;
    private final CaseRepository caseRepository;
    private final CandidateRepository candidateRepository;

    public AnalyticsService(PartnerRepository partnerRepository, CaseRepository caseRepository,
                            CandidateRepository candidateRepository) {
        this.partnerRepository = partnerRepository;
        this.caseRepository = caseRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> section(String section) {
        return switch (section) {
            case "overview" -> overview();
            case "detail" -> detail();
            case "efficiency" -> efficiency();
            default -> throw new ApiException.NotFound("Unknown analytics section: " + section);
        };
    }

    /** Overview — headline counts across partners, leads and cases. */
    private Map<String, Object> overview() {
        var partners = partnerRepository.findAll();
        var cases = caseRepository.findAll();
        var candidates = candidateRepository.findAll();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalPartners", partners.size());
        out.put("activePartners", partners.stream().filter(p -> "active".equals(p.getStatus())).count());
        out.put("inReviewPartners", partners.stream().filter(p -> "in_review".equals(p.getStatus())).count());
        out.put("discontinuedPartners", partners.stream().filter(p -> "discontinued".equals(p.getStatus())).count());
        out.put("partnersByType", countBy(partners, Partner::getPartnerType));
        out.put("totalLeads", candidates.size());
        out.put("leadsByStage", countBy(candidates, Candidate::getStage));
        out.put("totalCases", cases.size());
        out.put("casesByStatus", countBy(cases, CaseEntity::getStatus));
        return out;
    }

    /** Distributor detail — coverage by state and partner aging (onboarding cohort by year). */
    private Map<String, Object> detail() {
        var partners = partnerRepository.findAll();
        var active = partners.stream().filter(p -> "active".equals(p.getStatus())).toList();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("partnersByState", countBy(partners, Partner::getState));
        out.put("activePartnersByState", countBy(active, Partner::getState));
        out.put("partnersByStatus", countBy(partners, Partner::getStatus));
        out.put("onboardedByYear", countBy(active, p -> yearOf(p.getOnboardedAt())));
        out.put("deboardedByYear", countBy(
                partners.stream().filter(p -> "discontinued".equals(p.getStatus())).toList(),
                p -> yearOf(p.getDiscontinuedAt())));
        return out;
    }

    /** Onboarding efficiency — case outcomes and lead-to-active conversion. */
    private Map<String, Object> efficiency() {
        var cases = caseRepository.findAll();
        var candidates = candidateRepository.findAll();
        long total = cases.size();
        long approved = cases.stream().filter(c -> "approved".equals(c.getStatus())).count();
        long rejected = cases.stream().filter(c -> "rejected".equals(c.getStatus())).count();
        long flagged = cases.stream().filter(c -> "flagged".equals(c.getStatus())).count();
        long overdue = cases.stream().filter(CaseEntity::isOverdue).count();
        long decided = approved + rejected;
        long activeLeads = candidates.stream().filter(c -> "active".equals(c.getStage())).count();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalCases", total);
        out.put("approved", approved);
        out.put("rejected", rejected);
        out.put("flagged", flagged);
        out.put("overdue", overdue);
        out.put("approvalRatePct", decided == 0 ? 0 : Math.round(approved * 100.0 / decided));
        out.put("totalLeads", candidates.size());
        out.put("activeLeads", activeLeads);
        out.put("conversionRatePct", candidates.isEmpty() ? 0 : Math.round(activeLeads * 100.0 / candidates.size()));
        return out;
    }

    private static String yearOf(String humanDate) {
        if (humanDate == null) return "unknown";
        Matcher m = YEAR.matcher(humanDate);
        return m.find() ? m.group(1) : "unknown";
    }

    private <T> Map<String, Long> countBy(java.util.List<T> items, Function<T, String> key) {
        return items.stream()
                .map(key)
                .map(k -> k == null ? "unknown" : k)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
    }
}
