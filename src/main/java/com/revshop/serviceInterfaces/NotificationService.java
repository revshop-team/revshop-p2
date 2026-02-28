package com.revshop.serviceInterfaces;

import com.revshop.entity.Notification;
import com.revshop.entity.User;

import java.util.List;

public interface NotificationService {
    // Get unread notifications for seller
    List<Notification> getUnreadNotifications(User seller);

    // Mark notification as read
    void markAsRead(Long notificationId);

    // Create new notification
    void createNotification(User seller, String message);
}
