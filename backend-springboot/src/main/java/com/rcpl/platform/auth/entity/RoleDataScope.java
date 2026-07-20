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

/** Row-level data scope (all|own_region|own_state) per role, per data entity. */
@Entity
@Table(name = "role_data_scope")
@IdClass(RoleDataScope.Key.class)
@Getter
@Setter
@NoArgsConstructor
public class RoleDataScope {

    @Id
    @Column(name = "role_code", length = 32)
    private String roleCode;

    @Id
    @Column(length = 32)
    private String entity;

    @Column(nullable = false, length = 16)
    private String scope;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private String roleCode;
        private String entity;
    }
}
