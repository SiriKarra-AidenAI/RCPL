package com.rcpl.platform.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A persona (role) — the registry the Admin screen manages. */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleEntity {

    @Id
    @Column(length = 32)
    private String code;

    @Column(nullable = false, length = 128)
    private String label;

    @Column(name = "color_var", length = 64)
    private String colorVar;

    @Column(length = 512)
    private String blurb;
}
