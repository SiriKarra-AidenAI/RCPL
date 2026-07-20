package com.rcpl.platform.user;

import java.util.List;
import java.util.Map;

import com.rcpl.platform.auth.PermissionService;
import com.rcpl.platform.auth.RequireScreen;
import com.rcpl.platform.auth.dto.ScreenPermissionDto;
import com.rcpl.platform.auth.entity.RoleAnalyticsSection;
import com.rcpl.platform.auth.entity.RoleDataScope;
import com.rcpl.platform.auth.entity.RoleEntity;
import com.rcpl.platform.auth.entity.RoleScreenAccess;
import com.rcpl.platform.auth.repository.RoleAnalyticsSectionRepository;
import com.rcpl.platform.auth.repository.RoleDataScopeRepository;
import com.rcpl.platform.auth.repository.RoleRepository;
import com.rcpl.platform.auth.repository.RoleScreenAccessRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Role registry + admin edits to per-screen access, data scope, and analytics-section visibility. */
@RestController
@RequestMapping("/api/roles")
public class RoleAdminController {

    private final RoleRepository roleRepository;
    private final RoleScreenAccessRepository screenAccessRepo;
    private final RoleDataScopeRepository dataScopeRepo;
    private final RoleAnalyticsSectionRepository analyticsRepo;
    private final PermissionService permissionService;

    public RoleAdminController(RoleRepository roleRepository, RoleScreenAccessRepository screenAccessRepo,
                              RoleDataScopeRepository dataScopeRepo, RoleAnalyticsSectionRepository analyticsRepo,
                              PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.screenAccessRepo = screenAccessRepo;
        this.dataScopeRepo = dataScopeRepo;
        this.analyticsRepo = analyticsRepo;
        this.permissionService = permissionService;
    }

    public record RoleDto(String code, String label, String colorVar, String blurb) {
        static RoleDto from(RoleEntity r) {
            return new RoleDto(r.getCode(), r.getLabel(), r.getColorVar(), r.getBlurb());
        }
    }

    public record ScreenAccessPatch(@NotBlank String screenPath, boolean view, boolean manage) {}

    public record DataScopePatch(@NotBlank String entity, @NotBlank String scope) {}

    public record AnalyticsSectionsPatch(List<String> sections) {}

    @GetMapping
    @RequireScreen("/settings")
    public List<RoleDto> list() {
        return roleRepository.findAll().stream().map(RoleDto::from).toList();
    }

    @GetMapping("/{code}/access")
    @RequireScreen("/settings")
    public Map<String, ScreenPermissionDto> access(@PathVariable String code) {
        return permissionService.accessMap(code);
    }

    @PatchMapping("/{code}/access")
    @RequireScreen(value = "/settings", manage = true)
    @Transactional
    public Map<String, ScreenPermissionDto> patchAccess(@PathVariable String code,
                                                        @RequestBody ScreenAccessPatch patch) {
        RoleScreenAccess a = screenAccessRepo
                .findById(new RoleScreenAccess.Key(code, patch.screenPath()))
                .orElseGet(RoleScreenAccess::new);
        a.setRoleCode(code);
        a.setScreenPath(patch.screenPath());
        a.setCanView(patch.view());
        a.setCanManage(patch.manage());
        screenAccessRepo.save(a);
        return permissionService.accessMap(code);
    }

    @PatchMapping("/{code}/data-scope")
    @RequireScreen(value = "/settings", manage = true)
    @Transactional
    public DataScopePatch patchDataScope(@PathVariable String code, @RequestBody DataScopePatch patch) {
        RoleDataScope s = dataScopeRepo
                .findByRoleCodeAndEntity(code, patch.entity())
                .orElseGet(RoleDataScope::new);
        s.setRoleCode(code);
        s.setEntity(patch.entity());
        s.setScope(patch.scope());
        dataScopeRepo.save(s);
        return patch;
    }

    @PatchMapping("/{code}/analytics-sections")
    @RequireScreen(value = "/settings", manage = true)
    @Transactional
    public List<String> setAnalyticsSections(@PathVariable String code,
                                             @RequestBody AnalyticsSectionsPatch patch) {
        analyticsRepo.deleteAll(analyticsRepo.findByRoleCode(code));
        List<String> sections = patch.sections() == null ? List.of() : patch.sections();
        for (String section : sections) {
            RoleAnalyticsSection e = new RoleAnalyticsSection();
            e.setRoleCode(code);
            e.setSection(section);
            analyticsRepo.save(e);
        }
        return sections;
    }
}
