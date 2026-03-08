package com.revshop.repo;

import com.revshop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * @param orderIds
     * @return List<Payment>
     */
    List<Payment> findByOrder_OrderIdIn(List<Long> orderIds);
    Payment findByOrder_OrderId(Long orderId);
}
