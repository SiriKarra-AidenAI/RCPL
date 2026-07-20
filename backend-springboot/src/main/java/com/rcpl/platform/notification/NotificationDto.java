package com.rcpl.platform.notification;

/** Notification returned to the client (mirrors AppNotification in the contract). */
public record NotificationDto(
        String id,
        String title,
        String body,
        String href,
        String forRole,
        boolean read,
        String time
) {
    public static NotificationDto from(Notification n) {
        return new NotificationDto(n.getId(), n.getTitle(), n.getBody(), n.getHref(),
                n.getForRole(), n.isRead(), n.getTimeLabel());
    }
}
