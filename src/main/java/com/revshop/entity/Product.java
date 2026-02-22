package com.revshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq_gen")
    @SequenceGenerator(
            name = "product_seq_gen",
            sequenceName = "product_seq",
            allocationSize = 1
    )
    @Column(name = "product_id")
    private Long productId;

    /**
     * Many Products belong to ONE Seller (User with SELLER role)
     * Matches your original schema: seller_id FK -> users
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "product_name", length = 150, nullable = false)
    private String productName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "manufacturer", nullable = false, length = 150)
    private String manufacturer;

    @Column(name = "mrp")
    private Double mrp;

    @Column(name = "selling_price")
    private Double sellingPrice;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "stock_threshold")
    private Integer stockThreshold;

    /**
     * Many Products -> One Category
     * FK: category_id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 1 = Active, 0 = Inactive
     * Matches your original SQL schema
     */
    @Column(name = "is_active")
    private Integer isActive = 1;
}