package com.bupt.ta.service;

import com.bupt.ta.model.Notification;
import com.bupt.ta.repository.NotificationRepository;
import com.bupt.ta.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NotificationService {
    private final NotificationRepository notificationRepository = new NotificationRepository();

    public void createNotification(String userId, String type, String title, String message, String relatedEntityType, String relatedEntityId) {
        Notification notification = new Notification();
        notification.setNotificationId(IdGenerator.generateId("notif"));
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now().toString());

        List<Notification> notifications = notificationRepository.findAll();
        notifications.add(notification);
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> notifications = new ArrayList<>();
        for (Notification notification : notificationRepository.findAll()) {
            if (notification.getUserId().equals(userId)) {
                notifications.add(notification);
            }
        }
        notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        return notifications;
    }

    public int countUnread(String userId) {
        int unreadCount = 0;
        for (Notification notification : notificationRepository.findAll()) {
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                unreadCount++;
            }
        }
        return unreadCount;
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findAll();
        boolean changed = false;
        for (Notification notification : notifications) {
            if (notification.getUserId().equals(userId) && !notification.isRead()) {
                notification.setRead(true);
                changed = true;
            }
        }
        if (changed) {
            notificationRepository.saveAll(notifications);
        }
    }
}
