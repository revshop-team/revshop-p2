package com.revshop.serviceImpl;

import com.revshop.entity.Product;
import com.revshop.entity.User;
import com.revshop.repo.ProductRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.LowStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LowStockServiceImpl implements LowStockService {
    private static final Logger logger = LoggerFactory.getLogger(LowStockServiceImpl.class);


    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public LowStockServiceImpl(ProductRepository productRepository,
                               UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Product> getLowStockProducts(String sellerEmail) {
        logger.info("Fetching low stock products for seller email: {}", sellerEmail);


        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> {
                    logger.error("Seller not found with email: {}", sellerEmail);
                    return new RuntimeException("Seller not found");
                });
        logger.debug("Seller found with ID: {}", seller.getUserId());

        List<Product> lowStockProducts =  productRepository.findLowStockProductsBySeller(seller);
        logger.info("Low stock products fetched. Count: {}", lowStockProducts.size());

        return lowStockProducts;
    }
}