package com.rcpl.platform.auth.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Per-role, per-screen view/manage permission. */
@Entity
@Table(name = "role_screen_access")
@IdClass(RoleScreenAccess.Key.class)
@Getter
@Setter
@NoArgsConstructor
public class RoleScreenAccess {

    @Id
    @Column(name = "role_code", length = 32)
    private String roleCode;

    @Id
    @Column(name = "screen_path", length = 128)
    private String screenPath;

    @Column(name = "can_view", nullable = false)
    private boolean canView;

    @Column(name = "can_manage", nullable = false)
    private boolean canManage;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private String roleCode;
        private String screenPath;
    }
}
