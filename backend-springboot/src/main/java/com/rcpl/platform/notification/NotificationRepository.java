package com.rcpl.platform.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByForRoleIsNullOrForRoleOrderByCreatedAtDesc(String forRole);
    List<Notification> findAllByOrderByCreatedAtDesc();
}
