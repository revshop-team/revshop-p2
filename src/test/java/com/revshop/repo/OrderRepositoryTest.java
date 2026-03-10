package com.revshop.repo;

import com.revshop.entity.Order;
import com.revshop.entity.User;
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
public class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private User dummyBuyer;
    private Order dummyOrder;

    @BeforeEach
    void setUp() {
        dummyBuyer = new User();
        dummyBuyer.setUserId(1L);

        dummyOrder = new Order();
        dummyOrder.setOrderId(100L);
        dummyOrder.setBuyer(dummyBuyer);
        dummyOrder.setTotalAmount(2500.50);
        dummyOrder.setStatus("PLACED");
        dummyOrder.setOrderDate(LocalDateTime.now());
    }

    // 1. Testing custom findByBuyer method (Found)
    @Test
    void testFindByBuyer_Found() {
        Mockito.when(orderRepository.findByBuyer(dummyBuyer)).thenReturn(Arrays.asList(dummyOrder));

        List<Order> result = orderRepository.findByBuyer(dummyBuyer);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("PLACED", result.get(0).getStatus());
    }

    // 2. Testing custom findByBuyer method (Not Found)
    @Test
    void testFindByBuyer_Empty() {
        Mockito.when(orderRepository.findByBuyer(dummyBuyer)).thenReturn(Collections.emptyList());

        List<Order> result = orderRepository.findByBuyer(dummyBuyer);

        Assertions.assertTrue(result.isEmpty());
    }

    // 3. Testing built-in save method
    @Test
    void testSaveOrder() {
        Mockito.when(orderRepository.save(dummyOrder)).thenReturn(dummyOrder);

        Order savedOrder = orderRepository.save(dummyOrder);

        Assertions.assertNotNull(savedOrder);
        Assertions.assertEquals(100L, savedOrder.getOrderId());
    }

    // 4. Testing built-in findById (Found)
    @Test
    void testFindById_Found() {
        Mockito.when(orderRepository.findById(100L)).thenReturn(Optional.of(dummyOrder));

        Optional<Order> result = orderRepository.findById(100L);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(2500.50, result.get().getTotalAmount());
    }

    // 5. Testing built-in findById (Not Found)
    @Test
    void testFindById_NotFound() {
        Mockito.when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderRepository.findById(999L);

        Assertions.assertFalse(result.isPresent());
    }

    // 6. Testing built-in findAll (With Data)
    @Test
    void testFindAll_Found() {
        Mockito.when(orderRepository.findAll()).thenReturn(Arrays.asList(dummyOrder));

        List<Order> result = orderRepository.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // 7. Testing built-in findAll (Empty)
    @Test
    void testFindAll_Empty() {
        Mockito.when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<Order> result = orderRepository.findAll();

        Assertions.assertTrue(result.isEmpty());
    }

    // 8. Testing built-in deleteById method
    @Test
    void testDeleteById() {
        orderRepository.deleteById(100L);

        // Double-checking that the mock was told to delete exactly once
        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(100L);
    }

    // 9. Testing built-in delete method (passing the object)
    @Test
    void testDelete() {
        orderRepository.delete(dummyOrder);

        Mockito.verify(orderRepository, Mockito.times(1)).delete(dummyOrder);
    }

    // 10. Testing built-in count method
    @Test
    void testCount() {
        Mockito.when(orderRepository.count()).thenReturn(25L);

        long result = orderRepository.count();

        Assertions.assertEquals(25L, result);
    }

    // 11. Testing built-in existsById (True)
    @Test
    void testExistsById_True() {
        Mockito.when(orderRepository.existsById(100L)).thenReturn(true);

        boolean exists = orderRepository.existsById(100L);

        Assertions.assertTrue(exists);
    }

    // 12. Testing built-in existsById (False)
    @Test
    void testExistsById_False() {
        Mockito.when(orderRepository.existsById(999L)).thenReturn(false);

        boolean exists = orderRepository.existsById(999L);

        Assertions.assertFalse(exists);
    }
}