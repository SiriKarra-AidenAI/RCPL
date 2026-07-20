package com.rcpl.platform.common;

import org.springframework.http.HttpStatus;

/**
 * Base for domain exceptions that map cleanly onto an HTTP status + machine code.
 * Subclasses cover the statuses the API contract promises (400/403/404/409).
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    /** 404 — the entity does not exist or is out of the caller's data scope. */
    public static class NotFound extends ApiException {
        public NotFound(String message) {
            super(HttpStatus.NOT_FOUND, "not_found", message);
        }
    }

    /** 403 — authenticated but not permitted (screen permission or data scope). */
    public static class Forbidden extends ApiException {
        public Forbidden(String message) {
            super(HttpStatus.FORBIDDEN, "forbidden", message);
        }
    }

    /** 409 — a business rule / gate blocks the transition (e.g. replacement gate). */
    public static class Conflict extends ApiException {
        public Conflict(String code, String message) {
            super(HttpStatus.CONFLICT, code, message);
        }
    }

    /** 400 — the request is malformed or violates a precondition. */
    public static class BadRequest extends ApiException {
        public BadRequest(String message) {
            super(HttpStatus.BAD_REQUEST, "bad_request", message);
        }
    }
}
