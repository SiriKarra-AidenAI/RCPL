package com.rcpl.platform.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request/response payloads for the auth endpoints. */
public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record TokenResponse(String accessToken, UserProfileDto user) {
    }

    public record MeResponse(UserProfileDto user) {
    }
}
