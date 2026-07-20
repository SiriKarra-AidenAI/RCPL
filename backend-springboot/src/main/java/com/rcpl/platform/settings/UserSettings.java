package com.rcpl.platform.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Per-user preferences (My Settings): inbox integration, auto-forward, SLA window. */
@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserSettings {

    @Id
    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "inbox_provider", length = 24)
    private String inboxProvider; // gmail | outlook | null

    @Column(name = "inbox_address", length = 320)
    private String inboxAddress;

    @Column(name = "auto_forward_unmatched", nullable = false)
    private boolean autoForwardUnmatched = true;

    @Column(name = "sla_hours", nullable = false)
    private int slaHours = 24;
}
