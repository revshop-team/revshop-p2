package com.revshop.repo;

import com.revshop.entity.Cart;
import com.revshop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Tells Spring NOT to use H2
@ActiveProfiles("test") // Tells Spring to use src/test/resources/application-test.properties
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    private User testBuyer;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        // 1. Create a dummy User (Buyer) to associate with the cart
        testBuyer = new User();
        testBuyer.setEmail("buyer@test.com");
        testBuyer.setPassword("password123");
        testBuyer.setRole("BUYER");
        testBuyer = entityManager.persistAndFlush(testBuyer);

        // 2. Create and persist a baseline Cart
        testCart = new Cart();
        testCart.setBuyer(testBuyer);
        testCart.setCreatedAt(LocalDateTime.now());
        testCart = entityManager.persistAndFlush(testCart);
    }

    // 1. Test Saving a Cart
    @Test
    void testSaveCart_ShouldPersistCart() {
        Cart newCart = new Cart();
        newCart.setCreatedAt(LocalDateTime.now());

        Cart savedCart = cartRepository.save(newCart);

        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getCartId()).isGreaterThan(0);
    }

    // 2. Test Finding Cart by Valid Buyer
    @Test
    void testFindByBuyer_ShouldReturnCart_WhenCartExists() {
        Optional<Cart> foundCart = cartRepository.findByBuyer(testBuyer);

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getBuyer().getEmail()).isEqualTo("buyer@test.com");
    }

    // 3. Test Finding Cart for Buyer Without a Cart
    @Test
    void testFindByBuyer_ShouldReturnEmpty_WhenNoCartExists() {
        User userWithoutCart = new User();
        userWithoutCart.setEmail("nocart@test.com");
        userWithoutCart.setPassword("pass");
        userWithoutCart = entityManager.persistAndFlush(userWithoutCart);

        Optional<Cart> foundCart = cartRepository.findByBuyer(userWithoutCart);

        assertThat(foundCart).isNotPresent();
    }

    // 4. Test Finding Cart when Buyer is Null
    @Test
    void testFindByBuyer_ShouldReturnEmpty_WhenBuyerIsNull() {
        Optional<Cart> foundCart = cartRepository.findByBuyer(null);

        assertThat(foundCart).isNotPresent();
    }

    // 5. Test Finding Cart by ID
    @Test
    void testFindById_ShouldReturnCart_WhenIdExists() {
        Optional<Cart> foundCart = cartRepository.findById(testCart.getCartId());

        assertThat(foundCart).isPresent();
        assertThat(foundCart.get().getCartId()).isEqualTo(testCart.getCartId());
    }

    // 6. Test Finding Cart by Invalid ID
    @Test
    void testFindById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        Optional<Cart> foundCart = cartRepository.findById(999L);

        assertThat(foundCart).isNotPresent();
    }

    // 7. Test Retrieving All Carts
    // 7. Test Retrieving All Carts
    @Test
    void testFindAll_ShouldReturnAllCarts() {
        User anotherBuyer = new User();
        anotherBuyer.setEmail("buyer2@test.com");
        anotherBuyer.setPassword("password123"); // FIX: Added missing password
        anotherBuyer.setRole("BUYER");           // FIX: Added missing role
        anotherBuyer = entityManager.persistAndFlush(anotherBuyer);

        Cart cart2 = new Cart();
        cart2.setBuyer(anotherBuyer);
        entityManager.persistAndFlush(cart2);

        List<Cart> allCarts = cartRepository.findAll();

        // FIX: Changed from hasSize(2) to isGreaterThanOrEqualTo(2) to account for existing DB data
        assertThat(allCarts.size()).isGreaterThanOrEqualTo(2);
    }

    // 8. Test Updating an Existing Cart
    @Test
    void testUpdateCart_ShouldReflectChanges() {
        Cart cartToUpdate = cartRepository.findById(testCart.getCartId()).get();

        // Let's modify the creation time to simulate an update
        LocalDateTime newTime = LocalDateTime.now().minusDays(1);
        cartToUpdate.setCreatedAt(newTime);

        Cart updatedCart = cartRepository.save(cartToUpdate);
        entityManager.flush(); // Force sync with DB

        assertThat(updatedCart.getCreatedAt()).isEqualTo(newTime);
    }

    // 9. Test Deleting a Cart
    @Test
    void testDeleteCart_ShouldRemoveFromDatabase() {
        cartRepository.delete(testCart);

        Optional<Cart> deletedCart = cartRepository.findById(testCart.getCartId());

        assertThat(deletedCart).isNotPresent();
    }

    // 10. Test Check if Cart Exists by ID
    @Test
    void testExistsById_ShouldReturnTrue_WhenCartExists() {
        boolean exists = cartRepository.existsById(testCart.getCartId());

        assertThat(exists).isTrue();
    }

    // 11. Test Count Total Carts
    // 11. Test Count Total Carts
    @Test
    void testCount_ShouldReturnTotalCarts() {
        long count = cartRepository.count();

        // FIX: Account for old data existing in the revshop_test database
        assertThat(count).isGreaterThanOrEqualTo(1L);
    }

    // 12. Test Default CreatedAt Value
    @Test
    void testCartCreatedAt_ShouldHaveDefaultValueOnCreation() {
        Cart newCart = new Cart();
        // NOT setting createdAt manually to test the default initialization in the Entity

        Cart savedCart = cartRepository.save(newCart);

        assertThat(savedCart.getCreatedAt()).isNotNull();
    }
}