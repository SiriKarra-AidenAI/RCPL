package com.rcpl.platform.notification;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Bell notifications API (available to any authenticated user). */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> list(@AuthenticationPrincipal CurrentUser user) {
        return notificationService.listFor(user);
    }

    @PatchMapping("/{id}")
    public NotificationDto markRead(@PathVariable String id) {
        return notificationService.markRead(id);
    }

    @PostMapping("/read-all")
    public void markAllRead(@AuthenticationPrincipal CurrentUser user) {
        notificationService.markAllRead(user);
    }
}
