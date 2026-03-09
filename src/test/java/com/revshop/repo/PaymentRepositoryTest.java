package com.revshop.repo;

import com.revshop.entity.Order;
import com.revshop.entity.Payment;
import com.revshop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepository;

    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        // 1. Create a unique User for the Order
        User buyer = new User();
        buyer.setEmail("pay_user_" + System.currentTimeMillis() + "@test.com");
        buyer.setPassword("pass123");
        buyer.setRole("BUYER");
        buyer = entityManager.persistAndFlush(buyer);

        // 2. Create an Order (Payment needs an Order)
        testOrder = new Order();
        testOrder.setBuyer(buyer);
        testOrder.setStatus("PLACED");
        testOrder.setTotalAmount(1500.0);
        testOrder = entityManager.persistAndFlush(testOrder);

        // 3. Create a baseline Payment
        testPayment = new Payment();
        testPayment.setOrder(testOrder);
        testPayment.setPaymentMethod("UPI");
        testPayment.setPaymentStatus("SUCCESS");
        testPayment.setAmount(1500.0);
        testPayment.setPaidAt(LocalDateTime.now());
        testPayment = entityManager.persistAndFlush(testPayment);
    }

    // 1. Test Saving a Payment (Covers setPaymentMethod, setPaymentStatus, etc.)
    @Test
    void testSavePayment_ShouldPersistData() {
        assertThat(testPayment.getPaymentId()).isNotNull();
        assertThat(testPayment.getPaymentStatus()).isEqualTo("SUCCESS");
    }

    // 2. Test Custom Method: findByOrder_OrderId
    @Test
    void testFindByOrder_OrderId_ShouldReturnPayment() {
        Payment found = paymentRepository.findByOrder_OrderId(testOrder.getOrderId());

        assertThat(found).isNotNull();
        assertThat(found.getPaymentId()).isEqualTo(testPayment.getPaymentId());
    }

    // 3. Test Custom Method: findByOrder_OrderIdIn
    @Test
    void testFindByOrder_OrderIdIn_ShouldReturnList() {
        List<Payment> results = paymentRepository.findByOrder_OrderIdIn(List.of(testOrder.getOrderId()));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPaymentMethod()).isEqualTo("UPI");
    }

    // 4. Test Find By ID
    @Test
    void testFindById_ShouldReturnPayment() {
        Optional<Payment> found = paymentRepository.findById(testPayment.getPaymentId());
        assertThat(found).isPresent();
    }

    // 5. Test Update Payment Status (Critical for Coverage)
    @Test
    void testUpdatePaymentStatus_ShouldReflectInDB() {
        testPayment.setPaymentStatus("FAILED");
        Payment updated = paymentRepository.save(testPayment);
        entityManager.flush();

        assertThat(updated.getPaymentStatus()).isEqualTo("FAILED");
    }

    // 6. Test Find All
    @Test
    void testFindAll_ShouldReturnAtLeastOne() {
        List<Payment> all = paymentRepository.findAll();
        assertThat(all.size()).isGreaterThanOrEqualTo(1);
    }

    // 7. Test Delete Payment
    @Test
    void testDeletePayment_ShouldRemoveFromDB() {
        Long id = testPayment.getPaymentId();
        paymentRepository.delete(testPayment);
        entityManager.flush();

        Optional<Payment> found = paymentRepository.findById(id);
        assertThat(found).isNotPresent();
    }

    // 8. Test One-to-One Constraint (Unique Order ID)
    @Test
    void testDuplicateOrderPayment_ShouldFail() {
        Payment duplicatePay = new Payment();
        duplicatePay.setOrder(testOrder); // Same order as testPayment
        duplicatePay.setAmount(10.0);

        try {
            paymentRepository.save(duplicatePay);
            entityManager.flush();
        } catch (Exception e) {
            // Success: Oracle blocked the duplicate @OneToOne
            return;
        }
        assertThat(false).as("Expected unique constraint violation for Order ID").isTrue();
    }

    // 9. Test Null Order Failure
    @Test
    void testSavePayment_NullOrder_ShouldFail() {
        Payment badPay = new Payment();
        badPay.setOrder(null);

        try {
            paymentRepository.save(badPay);
            entityManager.flush();
        } catch (Exception e) {
            return;
        }
        assertThat(false).as("Expected null constraint violation").isTrue();
    }

    // 10. Test Count
    @Test
    void testCount_ShouldBePositive() {
        assertThat(paymentRepository.count()).isGreaterThanOrEqualTo(1L);
    }

    // 11. Test Payment with Different Method (CARD)
    @Test
    void testSavePayment_CardMethod() {
        Payment cardPay = new Payment();
        // Needs a new Order because of @OneToOne Unique constraint
        Order newOrder = new Order();
        newOrder.setBuyer(testOrder.getBuyer());
        newOrder = entityManager.persistAndFlush(newOrder);

        cardPay.setOrder(newOrder);
        cardPay.setPaymentMethod("CARD");
        cardPay.setPaymentStatus("PENDING");

        Payment saved = paymentRepository.save(cardPay);
        assertThat(saved.getPaymentMethod()).isEqualTo("CARD");
    }

    // 12. Test Finding Payment by Non-Existent Order ID
    @Test
    void testFindByOrder_OrderId_NotFound() {
        Payment found = paymentRepository.findByOrder_OrderId(-99L);
        assertThat(found).isNull();
    }
}