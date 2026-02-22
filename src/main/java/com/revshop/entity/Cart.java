package com.revshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_seq_gen")
    @SequenceGenerator(
            name = "cart_seq_gen",
            sequenceName = "cart_seq",
            allocationSize = 1
    )
    @Column(name = "cart_id")
    private Long cartId;

    // One Cart belongs to ONE Buyer (User)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", unique = true)
    private User buyer;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // One Cart -> Many Cart Items
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;
}