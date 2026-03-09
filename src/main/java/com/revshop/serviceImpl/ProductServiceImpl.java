package com.revshop.serviceImpl;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.ProductRepository;
import com.revshop.serviceInterfaces.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);


    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public void saveProduct(Product product, User seller) {
        logger.info("Saving new product: {} by seller: {}", product.getProductName(), seller.getEmail());

        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());
        product.setIsActive(1);

        productRepository.save(product);
        logger.debug("Product saved successfully: {}", product.getProductName());

    }

    @Override
    public List<Product> getProductBySeller(User seller) {
        logger.info("Fetching products for seller: {}", seller.getEmail());

        List<Product> products = productRepository.findBySeller(seller);
        logger.debug("Total products found for seller {} : {}", seller.getEmail(), products.size());
        return products;
    }

    @Override
    public List<Product> getAllCategoriesProduct() {
        logger.info("Fetching all products from database");
        List<Product> products = productRepository.findAll();

        logger.debug("Total products fetched: {}", products.size());

        return products;

    }

    @Override
    public Page<Product> getActiveProducts(Pageable pageable) {
        logger.info("Fetching active products with pagination");

        return productRepository.findByIsActive(1, pageable);
    }

    @Override
    public Product getProductById(Long id) {
        logger.info("Fetching product by id: {}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found with id: {}", id);
                    return new ProductNotFoundException("Product not found");
                });
    }

    @Override
    public List<Product> get12Products() {
        logger.info("Fetching top 12 active products");

        List<Product> products = productRepository
                .findByIsActiveTrue()
                .stream()
                .limit(12)
                .toList();
        logger.debug("Fetched {} products for homepage", products.size());
        return products;

    }

    // Save or update product
    @Override
    public Product saveOrUpdateProduct(Product product) {
        logger.info("Saving or updating product: {}", product.getProductName());

        Product savedProduct = productRepository.save(product);

        logger.debug("Product saved/updated successfully with id: {}", savedProduct.getProductId());

        return savedProduct;

    }

    @Override
    public void deleteProductById(Long id) {
        logger.info("Soft deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Product not found while deleting id: {}", id);
                    return new ProductNotFoundException("Product not found with id: " + id);
                });
        product.setIsActive(0); // set to inactive instead of deleting
        productRepository.save(product);
        logger.warn("Product set to inactive (soft delete) with id: {}", id);

    }

    @Override
    public List<Product> searchProducts(String keyword) {
        logger.info("Searching products with keyword: {}", keyword);

        if (keyword == null || keyword.isEmpty()) {
            logger.debug("Keyword empty, returning all products");

            return productRepository.findAll();
        }
        List<Product> products =  productRepository.findByProductNameContainingIgnoreCase(keyword);

        logger.debug("Search results count for '{}' : {}", keyword, products.size());

        return products;
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        logger.info("Fetching products by category id: {}", categoryId);

        List<Product> products =
                productRepository.findByCategory_CategoryId(categoryId);

        logger.debug("Products found in category {} : {}", categoryId, products.size());

        return products;
    }


    @Override
    public List<Product> getAllActiveProducts() {
        logger.info("Fetching all active products");

        List<Product> products = productRepository.findByIsActiveTrue();

        logger.debug("Total active products: {}", products.size());

        return products;
    }
    @Override
    public Page<Product> searchActiveProducts(String keyword, PageRequest pageable) {
        logger.info("Searching active products with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            logger.debug("Keyword empty, returning all active products");

            return productRepository.findByIsActiveTrue(pageable);
        }
        return productRepository.searchActiveProducts(keyword.trim(), pageable);
    }

    @Override
    public Page<Product> getActiveProductsByCategory(Long categoryId, PageRequest pageable) {
        logger.info("Fetching active products for category id: {}", categoryId);

        return productRepository
                .findByIsActiveTrueAndCategory_CategoryId(categoryId, pageable);
    }
}
