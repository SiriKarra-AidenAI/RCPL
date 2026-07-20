package com.rcpl.platform.auth;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.rcpl.platform.auth.dto.ScreenPermissionDto;
import com.rcpl.platform.auth.dto.UserProfileDto;
import com.rcpl.platform.auth.entity.RoleScreenAccess;
import com.rcpl.platform.auth.entity.UserEntity;
import com.rcpl.platform.auth.repository.RoleAnalyticsSectionRepository;
import com.rcpl.platform.auth.repository.RoleDataScopeRepository;
import com.rcpl.platform.auth.repository.RoleScreenAccessRepository;
import com.rcpl.platform.common.ApiException;
import org.springframework.stereotype.Service;

/**
 * Central authorization: per-screen view/manage permission, row-level data-scope lookup,
 * and analytics-section visibility. Backed by the role_* reference tables.
 */
@Service
public class PermissionService {

    private final RoleScreenAccessRepository screenAccessRepo;
    private final RoleDataScopeRepository dataScopeRepo;
    private final RoleAnalyticsSectionRepository analyticsRepo;

    public PermissionService(RoleScreenAccessRepository screenAccessRepo,
                             RoleDataScopeRepository dataScopeRepo,
                             RoleAnalyticsSectionRepository analyticsRepo) {
        this.screenAccessRepo = screenAccessRepo;
        this.dataScopeRepo = dataScopeRepo;
        this.analyticsRepo = analyticsRepo;
    }

    /** The role's full per-screen permission map (screen path → view/manage). */
    public Map<String, ScreenPermissionDto> accessMap(String roleCode) {
        Map<String, ScreenPermissionDto> map = new LinkedHashMap<>();
        for (RoleScreenAccess a : screenAccessRepo.findByRoleCode(roleCode)) {
            map.put(a.getScreenPath(), new ScreenPermissionDto(a.isCanView(), a.isCanManage()));
        }
        return map;
    }

    /** Permission for one screen (defaults to no access if the role has no row for it). */
    public ScreenPermissionDto can(String roleCode, String screenPath) {
        return screenAccessRepo.findById(new RoleScreenAccess.Key(roleCode, screenPath))
                .map(a -> new ScreenPermissionDto(a.isCanView(), a.isCanManage()))
                .orElse(new ScreenPermissionDto(false, false));
    }

    /** 403 unless the role may view the screen. */
    public void requireView(CurrentUser user, String screenPath) {
        if (!can(user.roleCode(), screenPath).view()) {
            throw new ApiException.Forbidden("No view access to " + screenPath);
        }
    }

    /** 403 unless the role may manage (write on) the screen. */
    public void requireManage(CurrentUser user, String screenPath) {
        if (!can(user.roleCode(), screenPath).manage()) {
            throw new ApiException.Forbidden("No manage access to " + screenPath);
        }
    }

    /** Row-level scope for a role + data entity, defaulting to 'all' when unset. */
    public String dataScope(String roleCode, String entity) {
        return dataScopeRepo.findByRoleCodeAndEntity(roleCode, entity)
                .map(s -> s.getScope())
                .orElse("all");
    }

    /** Analytics sections the role may see. */
    public Set<String> analyticsSections(String roleCode) {
        return analyticsRepo.findByRoleCode(roleCode).stream()
                .map(s -> s.getSection())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /** 403 unless the role may see the given analytics section. */
    public void requireAnalyticsSection(String roleCode, String section) {
        if (!analyticsSections(roleCode).contains(section)) {
            throw new ApiException.Forbidden("Analytics section not permitted: " + section);
        }
    }

    /** Build the public profile the SPA consumes, including the access map. */
    public UserProfileDto toProfile(UserEntity user) {
        return new UserProfileDto(
                user.getId(), user.getName(), user.getEmail(), user.getRoleCode(),
                user.getRegion(), user.getState(), user.isActive(),
                accessMap(user.getRoleCode()));
    }
}
