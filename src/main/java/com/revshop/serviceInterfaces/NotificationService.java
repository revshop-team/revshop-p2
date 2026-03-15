package com.revshop.serviceInterfaces;

import com.revshop.entity.Notification;
import java.util.List;

public interface NotificationService {

    void notifySellerOrderPlaced(Long orderId);

    List<Notification> getUserNotifications(String email);

    void markAsRead(Long notificationId);


    long getUnreadCount(String email);


    void deleteNotification(Long id, String email);

}