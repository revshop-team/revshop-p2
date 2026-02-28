package com.revshop.serviceImpl;

import com.revshop.entity.Notification;
import com.revshop.entity.User;
import com.revshop.repo.NotificationRepository;
import com.revshop.serviceInterfaces.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get unread notifications for seller
    @Override
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    // Mark notification as read
    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notif -> {
            notif.setIsRead(String.valueOf(true));
            notificationRepository.save(notif);
        });
    }

    // Create new notification
    @Override
    public void createNotification(User seller, String message) {
        Notification notif = new Notification();
        notif.setUser(seller);
        notif.setMessage("New order received!");
        notif.setIsRead(String.valueOf(false));
        notificationRepository.save(notif);    }
}
