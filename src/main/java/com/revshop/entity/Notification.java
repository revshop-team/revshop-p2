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
}