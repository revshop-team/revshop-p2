package com.revshop.service;

import com.revshop.entity.Product;
import com.revshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product addProduct(Product product) {
        product.setIsActive(1); // ensure active product
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long productId, Product product) {

        Product existing = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setProductName(product.getProductName());
        existing.setDescription(product.getDescription());
        existing.setManufacturer(product.getManufacturer());
        existing.setMrp(product.getMrp());
        existing.setSellingPrice(product.getSellingPrice());
        existing.setStock(product.getStock());
        existing.setStockThreshold(product.getStockThreshold());
        existing.setCategory(product.getCategory());

        return productRepository.save(existing);
    }

    @Override
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsActive(0); // soft delete instead of hard delete
        productRepository.save(product);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActive(1);
    }

    @Override
    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySeller_UserId(sellerId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}