package com.rcpl.platform.caseflow;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.caseflow.dto.CaseDtos.CaseDto;
import com.rcpl.platform.caseflow.dto.CaseDtos.ChannelDocRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.DecisionRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.DiscontinuationRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.FinanceDocRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.NoteRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.RaiseCaseRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.ScoreRequest;
import com.rcpl.platform.caseflow.dto.CaseDtos.ScoreResponse;
import com.rcpl.platform.caseflow.service.CaseService;
import com.rcpl.platform.caseflow.service.ScoringService;
import com.rcpl.platform.config.onboarding.OnboardingConfigService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Approvals queue + case workflow actions. */
@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;
    private final ScoringService scoringService;
    private final OnboardingConfigService onboardingConfig;

    public CaseController(CaseService caseService, ScoringService scoringService,
                          OnboardingConfigService onboardingConfig) {
        this.caseService = caseService;
        this.scoringService = scoringService;
        this.onboardingConfig = onboardingConfig;
    }

    @GetMapping
    @RequireScreen("/approvals")
    public List<CaseDto> list(@AuthenticationPrincipal CurrentUser user,
                              @RequestParam(defaultValue = "false") boolean mine) {
        return caseService.list(user, mine);
    }

    @GetMapping("/{code}")
    @RequireScreen("/approvals")
    public CaseDto get(@PathVariable String code) {
        return caseService.get(code);
    }

    @PostMapping
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto raise(@AuthenticationPrincipal CurrentUser user, @Valid @RequestBody RaiseCaseRequest req) {
        return caseService.raise(user, req);
    }

    @PostMapping("/{code}/decision")
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto decide(@AuthenticationPrincipal CurrentUser user, @PathVariable String code,
                          @Valid @RequestBody DecisionRequest req) {
        return caseService.decide(user, code, req.decision());
    }

    @PostMapping("/{code}/finance-doc")
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto financeDoc(@AuthenticationPrincipal CurrentUser user, @PathVariable String code,
                              @Valid @RequestBody FinanceDocRequest req) {
        return caseService.attachFinanceDoc(user, code, req.key(), req.fileName(), req.dataUrl());
    }

    @PostMapping("/{code}/channel-doc")
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto channelDoc(@AuthenticationPrincipal CurrentUser user, @PathVariable String code,
                              @Valid @RequestBody ChannelDocRequest req) {
        return caseService.attachChannelDoc(user, code, req.fileName());
    }

    @PostMapping("/{code}/discontinuation")
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto discontinuation(@AuthenticationPrincipal CurrentUser user, @PathVariable String code,
                                   @RequestBody DiscontinuationRequest req) {
        return caseService.linkDiscontinuation(user, code, req.form());
    }

    @PostMapping("/{code}/note")
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto note(@AuthenticationPrincipal CurrentUser user, @PathVariable String code,
                        @Valid @RequestBody NoteRequest req) {
        return caseService.addNote(user, code, req.author(), req.body());
    }

    @PostMapping("/{code}/onboarding-notified")
    @RequireScreen(value = "/approvals", manage = true)
    public CaseDto onboardingNotified(@PathVariable String code) {
        return caseService.markOnboardingNotified(code);
    }

    /**
     * Deterministic finance/channel scoring for the New Application wizard. Required investment and
     * infra threshold fall back to the server-side onboarding config when the request omits them.
     */
    @PostMapping("/score")
    @RequireScreen("/new-application")
    public ScoreResponse score(@RequestBody ScoreRequest req) {
        BigDecimal required = req.requiredInvestment() != null ? req.requiredInvestment()
                : onboardingConfig.threshold("REQUIRED_INVESTMENT", BigDecimal.valueOf(144.6));
        BigDecimal threshold = req.infraThreshold() != null ? req.infraThreshold()
                : onboardingConfig.threshold("INFRA_THRESHOLD", BigDecimal.valueOf(7.0));
        return new ScoreResponse(
                scoringService.financeSnapshot(req.ownFunds(), req.ccLimit(), required),
                scoringService.channelSnapshot(req.infraScore(), threshold));
    }
}
