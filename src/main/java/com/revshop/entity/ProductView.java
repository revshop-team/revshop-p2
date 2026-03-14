package com.revshop.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_view")
public class ProductView {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "product_view_seq_gen")
    @SequenceGenerator(
            name = "product_view_seq_gen",
            sequenceName = "product_view_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime viewTime = LocalDateTime.now();

    public Long getId() { return id; }

    public User getUser() { return user; }

    public Product getProduct() { return product; }

    public LocalDateTime getViewTime() { return viewTime; }

    public void setUser(User user) { this.user = user; }

    public void setProduct(Product product) { this.product = product; }

    public void setViewTime(LocalDateTime viewTime) {
        this.viewTime = viewTime;
    }
}