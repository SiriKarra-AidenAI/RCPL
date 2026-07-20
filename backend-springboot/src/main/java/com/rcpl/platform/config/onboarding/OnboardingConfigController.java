package com.rcpl.platform.config.onboarding;

import com.rcpl.platform.config.onboarding.OnboardingConfigService.OnboardingConfigDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Onboarding/scoring config (thresholds, DB categories, infra factors) — read by the wizard. */
@RestController
@RequestMapping("/api/config/onboarding")
public class OnboardingConfigController {

    private final OnboardingConfigService service;

    public OnboardingConfigController(OnboardingConfigService service) {
        this.service = service;
    }

    @GetMapping
    public OnboardingConfigDto get() {
        return service.get();
    }
}
