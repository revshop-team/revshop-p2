package com.revshop.serviceImpl;

import com.revshop.entity.*;
import com.revshop.exceptions.BuyerNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.NotificationService;
import com.revshop.serviceInterfaces.OrderService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final BuyerDetailsRepository buyerDetailsRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService  notificationService;
    private final NotificationRepository notificationRepository;
    public OrderServiceImpl(UserRepository userRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            OrderAddressRepository orderAddressRepository,
                            BuyerDetailsRepository buyerDetailsRepository,
                            PaymentRepository paymentRepository,
                            NotificationService notificationService,
                            NotificationRepository notificationRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.buyerDetailsRepository = buyerDetailsRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService=notificationService;
        this.notificationRepository=notificationRepository;
    }

    @Override
    @jakarta.transaction.Transactional
    public Order checkout(String buyerEmail,
                          String fullName,
                          String phone,
                          String addressLine1,
                          String addressLine2,
                          String city,
                          String state,
                          String pincode,
                          String paymentMethod) {

        logger.info("Checkout started for buyer: {}", buyerEmail);


        // GET BUYER
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> {
                    logger.error("Buyer not found with email: {}", buyerEmail);
                    return new RuntimeException("Buyer not found");
                });

        // GET CART
        Cart cart = cartRepository.findByBuyer(buyer)
                .orElseThrow(() -> {
                    logger.error("Cart not found for buyer: {}", buyerEmail);
                    return new RuntimeException("Cart not found");
                });

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            logger.warn("Checkout attempted with empty cart for buyer: {}", buyerEmail);

            throw new RuntimeException("Cart is empty");
        }

        // CREATE ORDER
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setStatus("PLACED");

        List<OrderItem> orderItems = new java.util.ArrayList<>();
        double totalAmount = 0.0;

        // CONVERT CARTITEMS → ORDERITEMS
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            logger.debug("Processing product {} with quantity {}", product.getProductName(), cartItem.getQuantity());


            if (product.getStock() < cartItem.getQuantity()) {
                logger.error("Insufficient stock for product: {}", product.getProductName());

                throw new RuntimeException("Insufficient stock for: " + product.getProductName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            logger.info("Stock updated for product {}. Remaining: {}",
                    product.getProductName(), product.getStock());

            if (product.getStock() <= product.getStockThreshold()) {
                logger.warn("Low stock alert for product {}", product.getProductName());

                Notification lowStockNotification = new Notification();
                lowStockNotification.setUser(product.getSeller());
                lowStockNotification.setMessage(
                        "Low stock alert: " + product.getProductName() +
                                " only " + product.getStock() + " left!"
                );
                lowStockNotification.setIsRead("N");
                lowStockNotification.setCreatedAt(java.time.LocalDateTime.now());

                notificationRepository.save(lowStockNotification);
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setSeller(product.getSeller());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getSellingPrice());

            totalAmount += cartItem.getProduct().getSellingPrice() * cartItem.getQuantity();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        orderRepository.save(order);
        logger.info("Order created successfully. OrderId: {}", order.getOrderId());

        // 🔔 NOTIFY SELLERS
        notificationService.notifySellerOrderPlaced(order.getOrderId());
        // Save address
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

        // CREATE PAYMENT

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(totalAmount);

        // Payment will be processed later in payment page
        if ("COD".equals(paymentMethod)) {

            // COD → Order placed immediately
            payment.setPaymentStatus("PENDING");
            order.setStatus("PLACED");

        } else {

            // Online payment → wait for gateway
            payment.setPaymentStatus("PENDING");
            order.setStatus("PENDING_PAYMENT");

        }

        paymentRepository.save(payment);
        logger.info("Payment record created for orderId: {}", order.getOrderId());

        Notification buyerNotification = new Notification();
        buyerNotification.setUser(buyer); // Buyer receives notification
        buyerNotification.setOrder(order);
        buyerNotification.setIsRead("N");

// Optional custom message (your entity auto-generates too)
        buyerNotification.setMessage("Your order #" + order.getOrderId() + " has been placed successfully!");

        notificationRepository.save(buyerNotification);
        // CLEAR CART PROPERLY
        cart.getCartItems().clear();
        cartRepository.save(cart);
        logger.info("Cart cleared after successful checkout for buyer: {}", buyerEmail);

        return order;
    }

    @Override
    @jakarta.transaction.Transactional
    public List<Order> getOrdersByBuyer(String email) {
        logger.info("Fetching orders for buyer: {}", email);


        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Buyer not found while fetching orders: {}", email);
                    return new BuyerNotFoundException("Buyer not found");
                });

        List<Order> orders = orderRepository.findByBuyer(buyer);

        orders.sort(Comparator.comparing(Order::getOrderDate).reversed());
        logger.debug("Total orders fetched for {} : {}", email, orders.size());


        return orders;
    }

//    @Override
//    public void updateOrderStatus(Long orderId, String status) {
//
//    }

    public void updateBuyerDetails(String email, BuyerDetails updatedDetails) {
        logger.info("Updating buyer details for email: {}", email);

        BuyerDetails existing =
                buyerDetailsRepository.findByUser_Email(email)
                                .orElseThrow(() -> {

                                    logger.error("Buyer details not found for email: {}", email);

                                    return new BuyerNotFoundException("buyer not found");
                                });

        existing.setFullName(updatedDetails.getFullName());
        existing.setGender(updatedDetails.getGender());
        existing.setDateOfBirth(updatedDetails.getDateOfBirth());
        existing.setPhone(updatedDetails.getPhone());

        buyerDetailsRepository.save(existing);
        logger.info("Buyer details updated successfully for {}", email);

    }
    @Override
    public void markAsDelivered(Long orderId) {
        logger.info("Mark order as delivered. OrderId: {}", orderId);


        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with id: {}", orderId);

                    return new RuntimeException("Order not found");
                });

        if ("PLACED".equals(order.getStatus())) {

            // 1️⃣ Update order status
            order.setStatus("DELIVERED");
            orderRepository.save(order);
            logger.info("Order delivered successfully. OrderId: {}", orderId);


            // 2️⃣ UPDATE PAYMENT STATUS FOR COD
            Payment payment = paymentRepository
                    .findByOrder_OrderId(orderId);

            if (payment != null && "PENDING".equals(payment.getPaymentStatus())) {

                payment.setPaymentStatus("SUCCESS");
                payment.setPaidAt(LocalDateTime.now());

                paymentRepository.save(payment);
                logger.info("COD payment marked as SUCCESS for orderId: {}", orderId);

            }

            // 2️⃣ CREATE BUYER NOTIFICATION (🔥 NEW)
            Notification notification = new Notification();
            notification.setUser(order.getBuyer()); // send to buyer
            notification.setOrder(order);
            notification.setMessage("Your order #" + order.getOrderId() + " has been delivered.");
            notification.setIsRead("N");
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
            logger.debug("Delivery notification sent to buyer for orderId: {}", orderId);

        }
    }
}