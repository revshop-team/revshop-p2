package com.revshop.repo;


import com.revshop.entity.Notification;
import com.revshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

}
