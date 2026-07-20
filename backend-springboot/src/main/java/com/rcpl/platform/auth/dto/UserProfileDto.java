package com.rcpl.platform.auth.dto;

import java.util.Map;

/** Public user profile returned to the SPA (mirrors User in the frontend contract; no password). */
public record UserProfileDto(
        String id,
        String name,
        String email,
        String roleCode,
        String region,
        String state,
        boolean isActive,
        Map<String, ScreenPermissionDto> access
) {
}
