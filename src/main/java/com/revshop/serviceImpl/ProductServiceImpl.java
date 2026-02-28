package com.revshop.serviceImpl;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.ProductRepository;
import com.revshop.serviceInterfaces.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
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
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Save or update product
    @Override
    public Product saveOrUpdateProduct(Product product) {

        return productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

}
