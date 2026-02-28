package com.revshop.repo;


import com.revshop.entity.Notification;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch unread notifications for a user
    List<Notification> findByUserAndIsRead(User user, String isRead);

    // Fetch all notifications for a user, newest first
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

}
