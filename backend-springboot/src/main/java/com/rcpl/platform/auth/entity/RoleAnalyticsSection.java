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

/** Which Analytics sections (overview|detail|efficiency) a role may see. */
@Entity
@Table(name = "role_analytics_section")
@IdClass(RoleAnalyticsSection.Key.class)
@Getter
@Setter
@NoArgsConstructor
public class RoleAnalyticsSection {

    @Id
    @Column(name = "role_code", length = 32)
    private String roleCode;

    @Id
    @Column(length = 32)
    private String section;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private String roleCode;
        private String section;
    }
}
