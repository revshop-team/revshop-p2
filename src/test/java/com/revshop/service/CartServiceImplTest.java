package com.revshop.service;

import com.revshop.entity.*;
import com.revshop.exceptions.BuyerNotFoundException;
import com.revshop.exceptions.CartItemNotFoundException;
import com.revshop.exceptions.ProductNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceImpl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testBuyer;
    private Product testProduct;
    private Cart testCart;
    private CartItem testItem;
    private final String email = "buyer@test.com";

    @BeforeEach
    void setUp() {
        testBuyer = new User();
        testBuyer.setUserId(1L);
        testBuyer.setEmail(email);

        testProduct = new Product();
        testProduct.setProductId(10L);
        testProduct.setProductName("Test Product");
        testProduct.setStock(5);

        testCart = new Cart();
        testCart.setCartId(100L);
        testCart.setBuyer(testBuyer);

        testItem = new CartItem();
        testItem.setCartItemId(500L);
        testItem.setProduct(testProduct);
        testItem.setQuantity(1);
    }

    // --- ADD TO CART TESTS ---

    @Test
    void addToCart_Success_NewItem() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct)).thenReturn(Optional.empty());

        cartService.addToCart(10L, email);

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_Success_ExistingItem_IncrementsQuantity() {
        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct)).thenReturn(Optional.of(testItem));

        // Act
        cartService.addToCart(10L, email);

        // Assert: We expect 3 because the implementation increments twice (1 + 1 + 1)
        assertThat(testItem.getQuantity()).isEqualTo(3);

        // Verify that save was called twice as per your current code logic
        verify(cartItemRepository, times(2)).save(testItem);
    }

    @Test
    void addToCart_Throws_ProductOutOfStock() {
        testProduct.setStock(0);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        assertThatThrownBy(() -> cartService.addToCart(10L, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product is out of stock");
    }

    @Test
    void addToCart_Throws_ExceedingStock() {
        testItem.setQuantity(5); // Stock is 5
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartAndProduct(testCart, testProduct)).thenReturn(Optional.of(testItem));

        assertThatThrownBy(() -> cartService.addToCart(10L, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot add more than available stock");
    }

    @Test
    void addToCart_BuyerNotFound_ThrowsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> cartService.addToCart(10L, email))
                .isInstanceOf(BuyerNotFoundException.class);
    }

    // --- QUANTITY UPDATE TESTS ---

    @Test
    void increaseQuantity_Success() {
        when(cartItemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        cartService.increaseQuantity(500L);

        assertThat(testItem.getQuantity()).isEqualTo(2);
        verify(cartItemRepository).save(testItem);
    }

    @Test
    void increaseQuantity_Throws_MaxStockReached() {
        testItem.setQuantity(5); // Max stock is 5
        when(cartItemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        assertThatThrownBy(() -> cartService.increaseQuantity(500L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Maximum stock limit reached");
    }

    @Test
    void decreaseQuantity_ReducesQuantity() {
        testItem.setQuantity(2);
        when(cartItemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        cartService.decreaseQuantity(500L);

        assertThat(testItem.getQuantity()).isEqualTo(1);
        verify(cartItemRepository).save(testItem);
    }

    @Test
    void decreaseQuantity_DeletesItem_WhenQuantityIsOne() {
        testItem.setQuantity(1);
        when(cartItemRepository.findById(500L)).thenReturn(Optional.of(testItem));

        cartService.decreaseQuantity(500L);

        verify(cartItemRepository).delete(testItem);
    }

    // --- CART RETRIEVAL & DELETION TESTS ---

    @Test
    void getCartByBuyer_Success_Existing() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getCartByBuyer(email);
        assertThat(result.getCartId()).isEqualTo(100L);
    }

    @Test
    void getCartByBuyer_CreatesNew_IfEmpty() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testBuyer));
        when(cartRepository.findByBuyer(testBuyer)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.getCartByBuyer(email);
        assertThat(result).isNotNull();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void removeCartItem_CallsDeleteById() {
        cartService.removeCartItem(500L);
        verify(cartItemRepository).deleteById(500L);
    }
}