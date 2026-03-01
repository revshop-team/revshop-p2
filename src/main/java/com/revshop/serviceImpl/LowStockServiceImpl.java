package com.revshop.serviceImpl;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repo.ProductRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.LowStockService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LowStockServiceImpl implements LowStockService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public LowStockServiceImpl(ProductRepository productRepository,
                               UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Product> getLowStockProducts(String sellerEmail) {

        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return productRepository.findLowStockProductsBySeller(seller);
    }
}