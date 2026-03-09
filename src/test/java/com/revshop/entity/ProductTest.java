package com.revshop.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {

        Product product = new Product();

        User user = new User();
        Category category = new Category();
        LocalDateTime now = LocalDateTime.now();

        product.setProductId(1L);
        product.setSeller(user);
        product.setProductName("Laptop");
        product.setDescription("Gaming laptop");
        product.setManufacturer("Dell");
        product.setMrp(100000.0);
        product.setDiscount(10.0);
        product.setSellingPrice(90000.0);
        product.setStock(20);
        product.setStockThreshold(5);
        product.setCategory(category);
        product.setCreatedAt(now);
        product.setIsActive(1);
        product.setImageName("laptop.png");

        assertEquals(1L, product.getProductId());
        assertEquals(user, product.getSeller());
        assertEquals("Laptop", product.getProductName());
        assertEquals("Gaming laptop", product.getDescription());
        assertEquals("Dell", product.getManufacturer());
        assertEquals(100000.0, product.getMrp());
        assertEquals(10.0, product.getDiscount());
        assertEquals(90000.0, product.getSellingPrice());
        assertEquals(20, product.getStock());
        assertEquals(5, product.getStockThreshold());
        assertEquals(category, product.getCategory());
        assertEquals(now, product.getCreatedAt());
        assertEquals(1, product.getIsActive());
        assertEquals("laptop.png", product.getImageName());
    }

    @Test
    void testAllArgsConstructor() {

        User user = new User();
        Category category = new Category();
        LocalDateTime now = LocalDateTime.now();

        Product product = new Product(
                1L,
                user,
                "Phone",
                "Smart phone",
                "Samsung",
                50000.0,
                5.0,
                47500.0,
                10,
                2,
                category,
                now,
                1,
                "phone.png"
        );

        assertEquals(1L, product.getProductId());
        assertEquals("Phone", product.getProductName());
        assertEquals("Samsung", product.getManufacturer());
        assertEquals(47500.0, product.getSellingPrice());
    }

    @Test
    void testBuilder() {

        User user = new User();
        Category category = new Category();

        Product product = Product.builder()
                .productId(2L)
                .seller(user)
                .productName("Tablet")
                .description("Android Tablet")
                .manufacturer("Lenovo")
                .mrp(20000.0)
                .discount(2.0)
                .sellingPrice(19600.0)
                .stock(15)
                .stockThreshold(3)
                .category(category)
                .isActive(1)
                .imageName("tablet.png")
                .build();

        assertNotNull(product);
        assertEquals(2L, product.getProductId());
        assertEquals("Tablet", product.getProductName());
        assertEquals("Lenovo", product.getManufacturer());
        assertEquals(19600.0, product.getSellingPrice());
        assertEquals("tablet.png", product.getImageName());
    }
}