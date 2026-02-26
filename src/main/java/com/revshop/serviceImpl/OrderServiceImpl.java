package com.revshop.serviceImpl;

import com.revshop.entity.*;
import com.revshop.exceptions.BuyerNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final BuyerDetailsRepository buyerDetailsRepository;

    public OrderServiceImpl(UserRepository userRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            OrderAddressRepository orderAddressRepository,
                            BuyerDetailsRepository buyerDetailsRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.buyerDetailsRepository = buyerDetailsRepository;
    }

    @Override
    @jakarta.transaction.Transactional
    public void checkout(String buyerEmail,
                         String fullName,
                         String phone,
                         String addressLine1,
                         String addressLine2,
                         String city,
                         String state,
                         String pincode) {

        // 1️⃣ Get buyer
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        // 2️⃣ Get cart
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3️⃣ Create order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setStatus("PLACED");

        List<OrderItem> orderItems = new java.util.ArrayList<>();
        double totalAmount = 0.0;

        // 4️⃣ Convert CartItems → OrderItems
        for (CartItem cartItem : cart.getCartItems()) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setSeller(cartItem.getSeller());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getSellingPrice());

            totalAmount += cartItem.getProduct().getSellingPrice() * cartItem.getQuantity();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        orderRepository.save(order);

        // 5️⃣ Save address
        OrderAddress address = new OrderAddress();
        address.setOrder(order);
        address.setAddressType("SHIPPING");
        address.setFullName(fullName);
        address.setPhone(phone);
        address.setAddressLine1(addressLine1);
        address.setAddressLine2(addressLine2);
        address.setCity(city);
        address.setState(state);
        address.setPincode(pincode);

        orderAddressRepository.save(address);

        // 6️⃣ CLEAR CART PROPERLY

        cart.getCartItems().clear();   // orphanRemoval = true will delete items
        cartRepository.save(cart);
    }

    @Override
    @jakarta.transaction.Transactional
    public List<Order> getOrdersByBuyer(String email) {

        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new BuyerNotFoundException("Buyer not found"));

        return orderRepository.findByBuyer(buyer);
    }

    public void updateBuyerDetails(String email, BuyerDetails updatedDetails) {

        BuyerDetails existing =
                buyerDetailsRepository.findByUser_Email(email)
                                .orElseThrow(() ->
                                        new BuyerNotFoundException("buyer not found"));

        existing.setFullName(updatedDetails.getFullName());
        existing.setGender(updatedDetails.getGender());
        existing.setDateOfBirth(updatedDetails.getDateOfBirth());
        existing.setPhone(updatedDetails.getPhone());

        buyerDetailsRepository.save(existing);
    }
}