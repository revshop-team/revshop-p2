package com.revshop.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductBuilderTest {

    @Test
    void testProductBuilderCreation() {

        User seller = new User();
        Category category = new Category();

        Product product = Product.builder()
                .productId(1L)
                .seller(seller)
                .productName("Laptop")
                .description("Gaming Laptop")
                .manufacturer("Dell")
                .mrp(100000.0)
                .discount(10.0)
                .sellingPrice(90000.0)
                .stock(20)
                .stockThreshold(5)
                .category(category)
                .isActive(1)
                .imageName("laptop.png")
                .build();

        assertNotNull(product);
        assertEquals(1L, product.getProductId());
        assertEquals("Laptop", product.getProductName());
        assertEquals("Dell", product.getManufacturer());
        assertEquals(90000.0, product.getSellingPrice());
        assertEquals(20, product.getStock());
        assertEquals("laptop.png", product.getImageName());
    }
}