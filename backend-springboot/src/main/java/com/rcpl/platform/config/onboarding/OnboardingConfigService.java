package com.rcpl.platform.config.onboarding;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Serves onboarding/scoring config so thresholds and reference lists live server-side. */
@Service
public class OnboardingConfigService {

    public record InfraItemDto(String key, String label) {}

    public record OnboardingConfigDto(
            Map<String, BigDecimal> thresholds, List<String> dbCategories, List<InfraItemDto> infraItems) {
    }

    private final OnboardingConfigRepository configRepo;
    private final DbCategoryRepository dbCategoryRepo;
    private final InfraItemRepository infraItemRepo;

    public OnboardingConfigService(OnboardingConfigRepository configRepo, DbCategoryRepository dbCategoryRepo,
                                   InfraItemRepository infraItemRepo) {
        this.configRepo = configRepo;
        this.dbCategoryRepo = dbCategoryRepo;
        this.infraItemRepo = infraItemRepo;
    }

    @Transactional(readOnly = true)
    public OnboardingConfigDto get() {
        Map<String, BigDecimal> thresholds = new LinkedHashMap<>();
        configRepo.findAll().forEach(c -> thresholds.put(c.getKey(), c.getValue()));
        List<String> dbCategories = dbCategoryRepo.findAllByOrderBySortOrderAsc().stream()
                .map(DbCategory::getCode).toList();
        List<InfraItemDto> infra = infraItemRepo.findAllByOrderBySortOrderAsc().stream()
                .map(i -> new InfraItemDto(i.getItemKey(), i.getLabel())).toList();
        return new OnboardingConfigDto(thresholds, dbCategories, infra);
    }

    /** A single threshold value with a fallback if unset. */
    @Transactional(readOnly = true)
    public BigDecimal threshold(String key, BigDecimal fallback) {
        return configRepo.findById(key).map(OnboardingConfigEntity::getValue).orElse(fallback);
    }
}
