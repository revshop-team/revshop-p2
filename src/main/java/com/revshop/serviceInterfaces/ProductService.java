package com.revshop.serviceInterfaces;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {


    void saveProduct(Product product, User seller);

    List<Product> getProductBySeller(User seller);

    List<Product> getAllCategoriesProduct();

    Page<Product> getActiveProducts(Pageable pageable);

    Product getProductById(Long id);

    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> getAllActiveProducts();
    Page<Product> searchActiveProducts(String keyword, PageRequest pageable);
    Page<Product> getActiveProductsByCategory(Long categoryId, PageRequest pageable);
}
