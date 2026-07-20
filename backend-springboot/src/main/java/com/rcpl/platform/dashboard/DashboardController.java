package com.rcpl.platform.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.candidate.CandidateRepository;
import com.rcpl.platform.caseflow.entity.CaseEntity;
import com.rcpl.platform.caseflow.repository.CaseRepository;
import com.rcpl.platform.grievance.repository.GrievanceRepository;
import com.rcpl.platform.notification.NotificationRepository;
import com.rcpl.platform.partner.PartnerRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Live persona counts for the Dashboard. The persona KPI/journey templates stay client-side;
 * this endpoint supplies the real numbers that overlay them.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CandidateRepository candidateRepository;
    private final CaseRepository caseRepository;
    private final NotificationRepository notificationRepository;
    private final GrievanceRepository grievanceRepository;
    private final PartnerRepository partnerRepository;

    public DashboardController(CandidateRepository candidateRepository, CaseRepository caseRepository,
                              NotificationRepository notificationRepository, GrievanceRepository grievanceRepository,
                              PartnerRepository partnerRepository) {
        this.candidateRepository = candidateRepository;
        this.caseRepository = caseRepository;
        this.notificationRepository = notificationRepository;
        this.grievanceRepository = grievanceRepository;
        this.partnerRepository = partnerRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public Map<String, Object> summary(@AuthenticationPrincipal CurrentUser user) {
        var candidates = candidateRepository.findAll();
        var cases = caseRepository.findAll();
        boolean admin = "admin".equals(user.roleCode());

        // Cases in this persona's queue (owner or ever-involved); admin sees all.
        var myCases = cases.stream()
                .filter(c -> admin || user.roleCode().equals(c.getOwnerRole())
                        || (c.getInvolvedRoles() != null && c.getInvolvedRoles().contains(user.roleCode())))
                .toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("leadsTotal", candidates.size());
        out.put("leadsByStage", groupCount(candidates, com.rcpl.platform.candidate.Candidate::getStage));
        out.put("myCasesTotal", myCases.size());
        out.put("myCasesFlagged", myCases.stream().filter(c -> "flagged".equals(c.getStatus())).count());
        out.put("myCasesOverdue", myCases.stream().filter(CaseEntity::isOverdue).count());
        out.put("casesByStatus", groupCount(cases, CaseEntity::getStatus));
        out.put("unreadNotifications",
                notificationRepository.findByForRoleIsNullOrForRoleOrderByCreatedAtDesc(user.roleCode())
                        .stream().filter(n -> !n.isRead()).count());
        out.put("openGrievances",
                grievanceRepository.findAll().stream().filter(g -> !"resolved".equals(g.getStatus())).count());
        out.put("activePartners",
                partnerRepository.findAll().stream().filter(p -> "active".equals(p.getStatus())).count());
        return out;
    }

    private <T> Map<String, Long> groupCount(java.util.List<T> items, java.util.function.Function<T, String> key) {
        Map<String, Long> m = new LinkedHashMap<>();
        for (T item : items) {
            String k = key.apply(item);
            m.merge(k == null ? "unknown" : k, 1L, Long::sum);
        }
        return m;
    }
}
