package com.rcpl.platform.auth;

import com.rcpl.platform.common.ApiException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Enforces {@link RequireScreen} on controller methods against the current user's permissions. */
@Aspect
@Component
public class ScreenSecurityAspect {

    private final PermissionService permissionService;

    public ScreenSecurityAspect(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Before("@annotation(requireScreen)")
    public void check(RequireScreen requireScreen) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser user)) {
            throw new ApiException.Forbidden("Authentication required");
        }
        if (requireScreen.manage()) {
            permissionService.requireManage(user, requireScreen.value());
        } else {
            permissionService.requireView(user, requireScreen.value());
        }
    }
}
