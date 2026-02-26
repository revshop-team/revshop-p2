package com.revshop.repo;

import com.revshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByOrder_OrderIdAndOrder_Buyer_UserIdAndProduct_ProductIdAndOrder_Status(
            Long orderId,
            Long buyerId,
            Long productId,
            String status
    );
}
