package com.revshop.serviceImpl;
import com.revshop.entity.Notification;
import com.revshop.entity.Order;
import com.revshop.entity.OrderItem;
import com.revshop.entity.User;
import com.revshop.repo.NotificationRepository;
import com.revshop.repo.OrderRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.NotificationService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);


    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   OrderRepository orderRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // 🔔 NOTIFY SELLERS WHEN ORDER IS PLACED
    @Override
    public void notifySellerOrderPlaced(Long orderId) {
        logger.info("Creating seller notifications for orderId: {}", orderId);


        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);
                    return new RuntimeException("Order not found");
                });

        // Use Set to avoid duplicate notifications for same seller
        Set<User> sellers = new HashSet<>();

        for (OrderItem item : order.getOrderItems()) {
            if (item.getSeller() != null) {
                sellers.add(item.getSeller());
            }
        }
        logger.debug("Total sellers to notify: {}", sellers.size());


        // Create notification for each seller
        for (User seller : sellers) {
            Notification notification = new Notification();
            notification.setUser(seller);
            notification.setOrder(order);
            notification.setMessage(
                    "New order placed for your product. Order ID: " + order.getOrderId()
            );
            notification.setIsRead("N");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
            logger.info("Notification created for sellerId: {} for orderId: {}", seller.getUserId(), orderId);
        }

    }

    @Override
    public List<Notification> getUserNotifications(String email) {
        logger.info("Fetching notifications for user email: {}", email);


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while fetching notifications: {}", email);

                    return new RuntimeException("User not found");
                });

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);

        logger.info("Total notifications fetched for {} : {}", email, notifications.size());

        return notifications;
    }

    @Override
    public void markAsRead(Long notificationId) {
        logger.info("Mark notification as read. NotificationId: {}", notificationId);


        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    logger.error("Notification not found with id: {}", notificationId);

                    return new RuntimeException("Notification not found");
                });

        notification.setIsRead("Y");
        notificationRepository.save(notification);
        logger.debug("Notification marked as read. NotificationId: {}", notificationId);

    }

    @Override
    public long getUnreadCount(String email) {
        logger.info("Fetching unread notification count for email: {}", email);


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found while counting unread notifications: {}", email);
                    return new RuntimeException("User not found");
                });

        long count  = notificationRepository.countByUserAndIsRead(user, "N");
        logger.debug("Unread notification count for {} : {}", email, count);

        return count;
    }
    public void deleteNotification(Long id, String email) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Notification not found"));

        if(notification.getUser().getEmail().equals(email)) {
            notificationRepository.delete(notification);
        }
    }
}