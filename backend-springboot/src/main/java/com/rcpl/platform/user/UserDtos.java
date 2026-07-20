package com.rcpl.platform.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request payloads for admin user management. (Responses reuse auth's UserProfileDto.) */
public final class UserDtos {

    private UserDtos() {}

    public record CreateUserRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank String roleCode,
            String region,
            String state,
            @NotBlank String password,
            Boolean isActive) {
    }

    public record UpdateUserRequest(
            String name,
            @Email String email,
            String roleCode,
            String region,
            String state,
            Boolean isActive,
            String password) {
    }
}
