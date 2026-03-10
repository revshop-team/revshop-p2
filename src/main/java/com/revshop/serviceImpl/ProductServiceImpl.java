package com.revshop.serviceImpl;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.ProductRepository;
import com.revshop.serviceInterfaces.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public void saveProduct(Product product, User seller) {
        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setIsActive(1);

        productRepository.save(product);
    }

    @Override
    public List<Product> getProductBySeller(User seller) {
        return productRepository.findBySeller(seller);
    }

    @Override
    public List<Product> getAllCategoriesProduct() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByIsActive(1, pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));
    }

    @Override
    public List<Product> get12Products() {
        return productRepository
                .findByIsActiveTrue()
                .stream()
                .limit(12)
                .toList();
    }

    // Save or update product
    @Override
    public Product saveOrUpdateProduct(Product product) {

        return productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        product.setIsActive(0); // set to inactive instead of deleting
        productRepository.save(product);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }


    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }
    @Override
    public Page<Product> searchActiveProducts(String keyword, PageRequest pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findByIsActiveTrue(pageable);
        }
        return productRepository.searchActiveProducts(keyword.trim(), pageable);
    }

    @Override
    public Page<Product> getActiveProductsByCategory(Long categoryId, PageRequest pageable) {
        return productRepository
                .findByIsActiveTrueAndCategory_CategoryId(categoryId, pageable);
    }
}