package com.revshop.repo;

import com.revshop.entity.Order;
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
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User testBuyer;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 1. Create a unique buyer for each run to avoid unique constraint issues in USERS table
        testBuyer = new User();
        testBuyer.setEmail("order_buyer_" + System.currentTimeMillis() + "@test.com");
        testBuyer.setPassword("securePass123");
        testBuyer.setRole("BUYER");
        testBuyer = entityManager.persistAndFlush(testBuyer);

        // 2. Create a baseline order
        testOrder = new Order();
        testOrder.setBuyer(testBuyer);
        testOrder.setOrderDate(LocalDateTime.now());
        testOrder.setTotalAmount(2500.0);
        testOrder.setStatus("PLACED");
        testOrder = entityManager.persistAndFlush(testOrder);
    }

    // 1. Test Saving an Order
    @Test
    void testSaveOrder_ShouldPersistOrder() {
        Order newOrder = new Order();
        newOrder.setBuyer(testBuyer);
        newOrder.setTotalAmount(500.0);
        newOrder.setStatus("SHIPPED");

        Order saved = orderRepository.save(newOrder);

        assertThat(saved.getOrderId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo("SHIPPED");
    }

    // 2. Test Custom Method: findByBuyer
    @Test
    void testFindByBuyer_ShouldReturnOrdersList() {
        List<Order> orders = orderRepository.findByBuyer(testBuyer);

        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getBuyer().getEmail()).isEqualTo(testBuyer.getEmail());
    }

    // 3. Test findByBuyer with User having no orders
    @Test
    void testFindByBuyer_ShouldReturnEmptyList_WhenNoOrders() {
        User freshUser = new User();
        freshUser.setEmail("no_orders_" + System.currentTimeMillis() + "@test.com");
        freshUser.setPassword("pass");
        freshUser = entityManager.persistAndFlush(freshUser);

        List<Order> orders = orderRepository.findByBuyer(freshUser);

        assertThat(orders).isEmpty();
    }

    // 4. Test Find By ID
    @Test
    void testFindById_ShouldReturnOrder() {
        Optional<Order> found = orderRepository.findById(testOrder.getOrderId());
        assertThat(found).isPresent();
        assertThat(found.get().getTotalAmount()).isEqualTo(2500.0);
    }

    // 5. Test Find All
    @Test
    void testFindAll_ShouldReturnAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders.size()).isGreaterThanOrEqualTo(1);
    }

    // 6. Test Update Order Status
    @Test
    void testUpdateOrderStatus_ShouldReflectInDB() {
        Order toUpdate = orderRepository.findById(testOrder.getOrderId()).get();
        toUpdate.setStatus("DELIVERED");

        Order updated = orderRepository.save(toUpdate);
        entityManager.flush();

        assertThat(updated.getStatus()).isEqualTo("DELIVERED");
    }

    // 7. Test Delete Order
    @Test
    void testDeleteOrder_ShouldRemoveFromDB() {
        Long id = testOrder.getOrderId();
        orderRepository.delete(testOrder);
        entityManager.flush();

        Optional<Order> found = orderRepository.findById(id);
        assertThat(found).isNotPresent();
    }

    // 8. Test Null Buyer Failure (Constraint Validation)
    @Test
    void testSaveOrder_NullBuyer_ShouldFail() {
        Order invalidOrder = new Order();
        invalidOrder.setBuyer(null); // nullable = false
        invalidOrder.setTotalAmount(100.0);

        try {
            orderRepository.save(invalidOrder);
            entityManager.flush();
        } catch (Exception e) {
            // Success: Constraint caught the null
            return;
        }
        assertThat(false).as("Expected ORA-01400 or similar but none occurred").isTrue();
    }

    // 9. Test Total Amount Boundary
    @Test
    void testSaveOrder_ZeroAmount() {
        Order freeOrder = new Order();
        freeOrder.setBuyer(testBuyer);
        freeOrder.setTotalAmount(0.0);
        freeOrder.setStatus("PLACED");

        Order saved = orderRepository.save(freeOrder);
        assertThat(saved.getTotalAmount()).isEqualTo(0.0);
    }

    // 10. Test Count Orders
    @Test
    void testCount_ShouldBePositive() {
        long count = orderRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }

    // 11. Test Order Date Persistence
    @Test
    void testOrderDate_ShouldBeAutomaticallySet() {
        Order dateTestOrder = new Order();
        dateTestOrder.setBuyer(testBuyer);
        dateTestOrder.setTotalAmount(10.0);
        // orderDate is initialized to LocalDateTime.now() in Entity

        Order saved = orderRepository.save(dateTestOrder);
        assertThat(saved.getOrderDate()).isNotNull();
    }

    // 12. Test Finding Order by Invalid ID
    @Test
    void testFindById_InvalidId_ShouldReturnEmpty() {
        Optional<Order> found = orderRepository.findById(-1L);
        assertThat(found).isNotPresent();
    }
}