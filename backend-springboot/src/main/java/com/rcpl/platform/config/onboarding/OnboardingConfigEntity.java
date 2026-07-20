package com.rcpl.platform.config.onboarding;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A single onboarding/scoring config value (e.g. INFRA_THRESHOLD, REQUIRED_INVESTMENT). */
@Entity
@Table(name = "onboarding_config")
@Getter
@Setter
@NoArgsConstructor
public class OnboardingConfigEntity {

    @Id
    @Column(name = "config_key", length = 64)
    private String key;

    @Column(name = "config_value", nullable = false)
    private BigDecimal value;
}
