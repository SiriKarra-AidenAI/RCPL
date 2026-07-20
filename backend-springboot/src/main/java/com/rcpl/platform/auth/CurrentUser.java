package com.rcpl.platform.auth;

/**
 * The authenticated principal placed in the security context by {@link JwtAuthFilter}.
 * Carries just enough identity to enforce screen permissions and data scope.
 */
public record CurrentUser(
        String id,
        String name,
        String email,
        String roleCode,
        String region,
        String state
) {
}
