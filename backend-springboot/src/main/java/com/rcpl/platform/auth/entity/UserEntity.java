package com.rcpl.platform.auth.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An application user. Passwords are stored BCrypt-hashed and never returned to clients. */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "role_code", nullable = false, length = 32)
    private String roleCode;

    @Column(length = 64)
    private String region;

    @Column(length = 64)
    private String state;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
