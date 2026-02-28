package com.revshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_gen")
    @SequenceGenerator(
            name = "notification_seq_gen",
            sequenceName = "notifications_seq",
            allocationSize = 1
    )
    @Column(name = "notification_id")
    private Long notificationId;

    // RECEIVER (CAN BE BUYER OR SELLER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message", length = 300)
    private String message;

    @Column(name = "is_read", length = 1)
    private String isRead = "N"; // N = Not Read, Y = Read

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Link to Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        if (order != null && order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            String productName = order.getOrderItems().get(0).getProduct().getProductName();
            return "Order placed for " + productName;
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}