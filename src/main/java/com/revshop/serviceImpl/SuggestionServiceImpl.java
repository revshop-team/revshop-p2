package com.revshop.serviceImpl;

import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.SuggestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    private final ProductViewRepo viewRepo;
    private final OrderItemRepository orderRepo;
    private final ProductRepository productRepo;

    public SuggestionServiceImpl(
            ProductViewRepo viewRepo,
            OrderItemRepository orderRepo,
            ProductRepository productRepo) {

        this.viewRepo = viewRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }


    // ✅ viewed suggestion
    @Override
    public List<Product> suggestByView(User user) {

        List<ProductView> views =
                viewRepo.findByUserOrderByViewTimeDesc(user);

        if (views.isEmpty()) {
            return List.of();
        }

        Product p = views.get(0).getProduct();

        Long catId =
                p.getCategory().getCategoryId();

        return productRepo
                .findByCategoryCategoryIdAndIsActive(
                        catId,
                        1
                );
    }


    // ✅ ordered suggestion
    @Override
    public List<Product> suggestByOrder(User user) {

        List<OrderItem> orders =
                orderRepo
                        .findByOrderBuyerOrderByOrderItemIdDesc(user);

        if (orders.isEmpty()) {
            return List.of();
        }

        Product p = orders.get(0).getProduct();

        Long catId =
                p.getCategory().getCategoryId();

        return productRepo
                .findByCategoryCategoryIdAndIsActive(
                        catId,
                        1
                );
    }
}