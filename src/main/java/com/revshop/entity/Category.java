package com.revshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories",
uniqueConstraints = {
@UniqueConstraint(columnNames = "category_name")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq_gen")
    @SequenceGenerator(
            name = "category_seq_gen",
            sequenceName = "categories_seq",
            allocationSize = 1
    )
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", unique=true,nullable = false, length = 100)
    private String categoryName;

    @Column(name = "description", length = 255)
    private String description;

    /**
     * Inverse side of relationship
     * One Category -> Many Products
     * mappedBy MUST match field name in Product entity
     */
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = false,
            fetch = FetchType.LAZY
    )
    private List<Product> products = new ArrayList<>();

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}