package com.rcpl.platform.common;

import java.time.Instant;
import java.util.Map;

/**
 * Consistent error body for every {@code /api/*} and {@code /auth/*} failure.
 *
 * @param status     HTTP status code (400/401/403/404/409/500)
 * @param code       short machine-readable code, e.g. "not_found", "gate_blocked"
 * @param message    human-readable message
 * @param fieldErrors optional field -> message map for validation failures (may be null)
 * @param timestamp  when the error was produced
 */
public record ApiError(
        int status,
        String code,
        String message,
        Map<String, String> fieldErrors,
        Instant timestamp
) {
    public static ApiError of(int status, String code, String message) {
        return new ApiError(status, code, message, null, Instant.now());
    }

    public static ApiError of(int status, String code, String message, Map<String, String> fieldErrors) {
        return new ApiError(status, code, message, fieldErrors, Instant.now());
    }
}
