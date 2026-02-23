package com.revshop.service;

import com.revshop.entity.Product;
import java.util.List;

public interface ProductService {

    Product addProduct(Product product);

    Product updateProduct(Long productId, Product product);

    void deactivateProduct(Long productId); // soft delete (better)

    List<Product> getAllActiveProducts(); // for buyers

    List<Product> getProductsBySeller(Long sellerId); // seller dashboard

    List<Product> searchProducts(String keyword);

    List<Product> getProductsByCategory(Long categoryId);

    Product getProductById(Long productId);
}