package com.rcpl.platform.notification;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A topbar notification (AppNotification in the contract). */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @Column(length = 64)
    private String id;

    @Column(length = 300)
    private String title;

    @Column(length = 2000)
    private String body;

    @Column(length = 256)
    private String href;

    @Column(name = "for_role", length = 32)
    private String forRole;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    @Column(name = "time_label", length = 64)
    private String timeLabel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
