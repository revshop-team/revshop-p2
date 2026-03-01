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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

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

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Use Set to avoid duplicate notifications for same seller
        Set<User> sellers = new HashSet<>();

        for (OrderItem item : order.getOrderItems()) {
            if (item.getSeller() != null) {
                sellers.add(item.getSeller());
            }
        }

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
        }
    }

    @Override
    public List<Notification> getUserNotifications(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead("Y");
        notificationRepository.save(notification);
    }

    @Override
    public long getUnreadCount(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return notificationRepository.countByUserAndIsRead(user, "N");
    }
}