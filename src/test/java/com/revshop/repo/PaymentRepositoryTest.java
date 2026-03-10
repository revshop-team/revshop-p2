package com.revshop.repo;

import com.revshop.entity.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

    private Payment dummyPayment;

    @BeforeEach
    void setUp() {
        dummyPayment = new Payment();
        dummyPayment.setPaymentId(1L);
        dummyPayment.setPaymentMethod("UPI");
        dummyPayment.setPaymentStatus("SUCCESS");
        dummyPayment.setAmount(1500.00);
        dummyPayment.setPaidAt(LocalDateTime.now());
    }

    // 1. Testing findByOrder_OrderIdIn with a match
    @Test
    void testFindByOrder_OrderIdIn_Found() {
        List<Long> orderIds = Arrays.asList(101L, 102L);
        Mockito.when(paymentRepository.findByOrder_OrderIdIn(orderIds)).thenReturn(Arrays.asList(dummyPayment));

        List<Payment> result = paymentRepository.findByOrder_OrderIdIn(orderIds);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("UPI", result.get(0).getPaymentMethod());
    }

    // 2. Testing findByOrder_OrderIdIn with no match
    @Test
    void testFindByOrder_OrderIdIn_Empty() {
        List<Long> orderIds = Arrays.asList(999L);
        Mockito.when(paymentRepository.findByOrder_OrderIdIn(orderIds)).thenReturn(Collections.emptyList());

        List<Payment> result = paymentRepository.findByOrder_OrderIdIn(orderIds);

        Assertions.assertTrue(result.isEmpty());
    }

    // 3. Testing findByOrder_OrderId with a match
    @Test
    void testFindByOrder_OrderId_Found() {
        Long orderId = 101L;
        Mockito.when(paymentRepository.findByOrder_OrderId(orderId)).thenReturn(dummyPayment);

        Payment result = paymentRepository.findByOrder_OrderId(orderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1500.00, result.getAmount());
    }

    // 4. Testing findByOrder_OrderId with no match
    @Test
    void testFindByOrder_OrderId_NotFound() {
        Long orderId = 999L;
        Mockito.when(paymentRepository.findByOrder_OrderId(orderId)).thenReturn(null);

        Payment result = paymentRepository.findByOrder_OrderId(orderId);

        Assertions.assertNull(result);
    }

    // 5. Testing built-in save method
    @Test
    void testSavePayment() {
        Mockito.when(paymentRepository.save(dummyPayment)).thenReturn(dummyPayment);

        Payment result = paymentRepository.save(dummyPayment);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("SUCCESS", result.getPaymentStatus());
    }

    // 6. Testing built-in findById with a match
    @Test
    void testFindById_Found() {
        Mockito.when(paymentRepository.findById(1L)).thenReturn(Optional.of(dummyPayment));

        Optional<Payment> result = paymentRepository.findById(1L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1L, result.get().getPaymentId());
    }

    // 7. Testing built-in findById with no match
    @Test
    void testFindById_NotFound() {
        Mockito.when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentRepository.findById(99L);

        Assertions.assertFalse(result.isPresent());
    }

    // 8. Testing built-in findAll with data
    @Test
    void testFindAll_Found() {
        Mockito.when(paymentRepository.findAll()).thenReturn(Arrays.asList(dummyPayment));

        List<Payment> result = paymentRepository.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // 9. Testing built-in findAll when empty
    @Test
    void testFindAll_Empty() {
        Mockito.when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        List<Payment> result = paymentRepository.findAll();

        Assertions.assertTrue(result.isEmpty());
    }

    // 10. Testing built-in deleteById method
    @Test
    void testDeleteById() {
        paymentRepository.deleteById(1L);

        // Verify that the delete method was actually called exactly one time
        Mockito.verify(paymentRepository, Mockito.times(1)).deleteById(1L);
    }

    // 11. Testing built-in count method
    @Test
    void testCount() {
        Mockito.when(paymentRepository.count()).thenReturn(5L);

        long result = paymentRepository.count();

        Assertions.assertEquals(5L, result);
    }

    // 12. Testing built-in existsById method
    @Test
    void testExistsById() {
        Mockito.when(paymentRepository.existsById(1L)).thenReturn(true);

        boolean result = paymentRepository.existsById(1L);

        Assertions.assertTrue(result);
    }
}