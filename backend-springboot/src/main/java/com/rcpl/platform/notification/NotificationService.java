package com.rcpl.platform.notification;

import java.util.List;

import com.rcpl.platform.auth.CurrentUser;
import com.rcpl.platform.common.Ids;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Bell notifications: role-targeted feed, mark-read, and a push helper for other services. */
@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> listFor(CurrentUser user) {
        return repository.findByForRoleIsNullOrForRoleOrderByCreatedAtDesc(user.roleCode())
                .stream().map(NotificationDto::from).toList();
    }

    @Transactional
    public NotificationDto markRead(String id) {
        Notification n = repository.findById(id).orElseThrow();
        n.setRead(true);
        return NotificationDto.from(repository.save(n));
    }

    @Transactional
    public void markAllRead(CurrentUser user) {
        for (Notification n : repository.findByForRoleIsNullOrForRoleOrderByCreatedAtDesc(user.roleCode())) {
            if (!n.isRead()) {
                n.setRead(true);
                repository.save(n);
            }
        }
    }

    /** Create a notification (used by grievance/case flows). */
    @Transactional
    public Notification push(String title, String body, String href, String forRole) {
        Notification n = new Notification();
        n.setId(Ids.newId("n"));
        n.setTitle(title);
        n.setBody(body);
        n.setHref(href);
        n.setForRole(forRole);
        n.setRead(false);
        n.setTimeLabel("just now");
        return repository.save(n);
    }
}
