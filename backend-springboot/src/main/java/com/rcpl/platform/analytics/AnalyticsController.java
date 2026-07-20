package com.rcpl.platform.analytics;

import java.util.Map;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.auth.PermissionService;
import com.rcpl.platform.auth.RequireScreen;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Analytics aggregates, screen-gated and section-gated per persona. */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final PermissionService permissionService;

    public AnalyticsController(AnalyticsService analyticsService, PermissionService permissionService) {
        this.analyticsService = analyticsService;
        this.permissionService = permissionService;
    }

    @GetMapping("/{section}")
    @RequireScreen("/analytics")
    public Map<String, Object> section(@AuthenticationPrincipal CurrentUser user, @PathVariable String section) {
        permissionService.requireAnalyticsSection(user.roleCode(), section);
        return analyticsService.section(section);
    }
}
