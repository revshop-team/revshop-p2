package com.revshop.controller;
import com.revshop.entity.*;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/buyer")
public class BuyerController {
    private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final BuyerService buyerService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private NotificationService notificationService;
    private OrderRepository orderRepository;
    private final ProductViewRepo productViewRepo;
    private final SuggestionService suggestionService;


    public BuyerController(ProductService productService, CartService cartService,
                           OrderService orderService, CategoryService categoryService,
                           BuyerService buyerService, ReviewService reviewService,
                           ReviewRepository reviewRepository, FavouriteRepository favouriteRepository,
                           UserRepository userRepository, OrderRepository orderRepository,PaymentRepository paymentRepository,NotificationService notificationService, ProductViewRepo productViewRepo,SuggestionService suggestionService) {

        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.buyerService = buyerService;
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.favouriteRepository = favouriteRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService=notificationService;
        this.orderRepository=orderRepository;
        this.productViewRepo = productViewRepo;
        this.suggestionService = suggestionService;
    }

    // buyer's home page
    @GetMapping("/home")
    public String buyerHome(Authentication authentication, Model model) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // latest products
        PageRequest pageable = PageRequest.of(0, 8, Sort.by("createdAt").descending());
        Page<Product> latestProducts = productService.getActiveProducts(pageable);

        List<Product> purchaseSuggestions = suggestionService.suggestByOrder(user);
        List<Product> viewSuggestions = suggestionService.suggestByView(user);

        model.addAttribute("latestProducts", latestProducts.getContent());
        model.addAttribute("purchaseSuggestions", purchaseSuggestions);
        model.addAttribute("viewSuggestions", viewSuggestions);

        // 🔹 ADD THIS:
        String buyerName = user.getBuyerDetails() != null
                ? user.getBuyerDetails().getFullName()
                : "Buyer"; // fallback if null
        model.addAttribute("buyerName", buyerName);

        return "buyer/home";
    }

    // buyer profile
    @GetMapping("/profile")
    public String viewProfile(@RequestParam(defaultValue = "false") boolean edit,
                              Authentication authentication,
                              Model model) {

        String email = authentication.getName();
        logger.info("Buyer profile requested for {}", email);

        // Existing profile logic
        BuyerDetails buyerDetails =
                buyerService.getBuyerDetailsByEmail(email);

        // New stats logic
        List<Order> orders =
                orderService.getOrdersByBuyer(email);

        int totalOrders = orders.size();

        double totalSpending = 0;
        for (Order order : orders) {
            totalSpending += order.getTotalAmount();
        }

        // Add everything to model (KEEP + ADD)
        model.addAttribute("buyerDetails", buyerDetails);
        model.addAttribute("editMode", edit);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSpending", totalSpending);
        logger.debug("Orders: {}, Spending: {}", totalOrders, totalSpending);

        return "buyer/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute BuyerDetails buyerDetails,Model model) {

        String  email = authentication.getName();
        logger.info("Updating buyer profile for {}", email);

        try {


            buyerService.updateBuyerDetails(email, buyerDetails);
            logger.info("Buyer profile updated successfully for {}", email);

            return "redirect:/buyer/profile";

        } catch (Exception e) {

            logger.error("Failed to update profile for {}", email, e);

            List<Order> orders =
                    orderService.getOrdersByBuyer(email);

            int totalOrders = orders.size();

            double totalSpending = 0;
            for (Order order : orders) {
                totalSpending += order.getTotalAmount();
            }

            model.addAttribute("editMode", true);
            model.addAttribute("buyerDetails", buyerDetails);
            model.addAttribute("error",
                    "Phone already exists or invalid data");
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("totalSpending", totalSpending);

            return "buyer/profile";
        }





    }


    //  View all products exist in RevShop
    // View all products with Search + Category + Sorting + Pagination (SAFE UPGRADE)
    @GetMapping("/products")
    public String viewProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "8") int size,
                               @RequestParam(defaultValue = "default") String sort,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long categoryId,
                               Authentication authentication,
                               Model model) {
        logger.info("Buyer viewing products page={} sort={} keyword={}",
                page, sort, keyword);

        Sort sorting = Sort.unsorted();

        if (sort.equals("priceAsc")) {
            sorting = Sort.by("sellingPrice").ascending();
        } else if (sort.equals("priceDesc")) {
            sorting = Sort.by("sellingPrice").descending();
        } else if (sort.equals("newest")) {
            sorting = Sort.by("createdAt").descending();
        }
        // Prevent negative page crash
        int safePage = Math.max(page, 0);
        PageRequest pageable = PageRequest.of(safePage, size, sorting);

        Page<Product> productPage;

        // 🔎 PRIORITY 1: Search by keyword
        if (keyword != null && !keyword.isBlank()) {
            productPage = productService.searchActiveProducts(keyword, pageable);

            // 📂 PRIORITY 2: Filter by category
        } else if (categoryId != null) {
            productPage = productService.getActiveProductsByCategory(categoryId, pageable);

            // 🛒 DEFAULT: All active products
        } else {
            productPage = productService.getActiveProducts(pageable);
        }

        // ⭐ EXISTING FAVOURITE LOGIC (UNCHANGED)
        String email = authentication.getName();
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Favourite> favourites = favouriteRepository.findByBuyer(buyer);

        Set<Long> favouriteProductIds = favourites.stream()
                .map(fav -> fav.getProduct().getProductId())
                .collect(Collectors.toSet());

        // 📂 ADD categories for UI filter (NEW)
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("categories", categories); // NEW
        model.addAttribute("favouriteProductIds", favouriteProductIds);

        return "buyer/products";
    }

    // view product details
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Long id, Model model,Authentication authentication) {

        Product product = productService.getProductById(id);

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        ProductView v = new ProductView();
        v.setUser(user);
        v.setProduct(product);
        v.setViewTime(LocalDateTime.now());

        productViewRepo.save(v);

        List<Review> reviews =
                reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(id);

        Double avgRating =
                reviewRepository.getAverageRatingByProductId(id);

        Long reviewCount =
                reviewRepository.countByProduct_ProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating != null ? avgRating : 0);
        model.addAttribute("reviewCount", reviewCount);

        return "buyer/product-details";
    }

    // ADD ITEM TO CART
    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {

        String buyerEmail = authentication.getName();
        logger.info("Buyer {} adding product {} to cart", buyerEmail, id);

        Product product = productService.getProductById(id);


        try {

            cartService.addToCart(id, buyerEmail);
            logger.info("Product {} added to cart", product.getProductName());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    product.getProductName() + " added to cart 🛒"
            );

        } catch (RuntimeException e) {
            logger.error("Failed to add product {} to cart: {}", id, e.getMessage());

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        return "redirect:/buyer/products";
    }

    // SEE CART
    @GetMapping("/cart")
    public String viewCart(Model model,
                           Authentication authentication) {

        String buyerEmail = authentication.getName();
        logger.info("Viewing cart for {}", buyerEmail);

        Cart cart = cartService.getCartByBuyer(buyerEmail);

        model.addAttribute("cart", cart);

        return "buyer/cart";
    }

    // increase the cart items quantity
    @GetMapping("/cart/increase/{id}")
    public String increaseQty(@PathVariable Long id) {

        cartService.increaseQuantity(id);

        return "redirect:/buyer/cart";
    }

    // DECREASE THE CART ITEMS QUANTITY
    @GetMapping("/cart/decrease/{id}")
    public String decreaseQty(@PathVariable Long id) {

        cartService.decreaseQuantity(id);

        return "redirect:/buyer/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id) {
        cartService.removeCartItem(id);
        return "redirect:/buyer/cart";
    }

    // CHECKOUT PAGE RENDER'S
    @GetMapping("/cart/checkout")
    public String checkoutPage(Authentication authentication,
                               Model model) {

        String buyerEmail = authentication.getName();
        Cart cart = cartService.getCartByBuyer(buyerEmail);
        model.addAttribute("cart", cart);

        return "buyer/checkout";
    }

    // CHECKOUT THE CART ITEMS
    @PostMapping("/cart/checkout")
    public String checkout(Authentication authentication,
                           @RequestParam String fullName,
                           @RequestParam String phone,
                           @RequestParam String addressLine1,
                           @RequestParam String addressLine2,
                           @RequestParam String city,
                           @RequestParam String state,
                           @RequestParam String pincode,
                           @RequestParam String paymentMethod,
                           RedirectAttributes redirectAttributes) {

        String buyerEmail = authentication.getName();
        logger.info("Checkout started for {}", buyerEmail);

        Cart cart = cartService.getCartByBuyer(buyerEmail);
        if (cart == null || cart.getCartItems().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your cart is empty. Please add items before checkout."
            );
            return "redirect:/buyer/products";
        }
        Order order=orderService.checkout(
                buyerEmail,
                fullName,
                phone,
                addressLine1,
                addressLine2,
                city,
                state,
                pincode,
                paymentMethod
        );
        logger.info("Order created with ID {}", order.getOrderId());

//        redirectAttributes.addFlashAttribute(
//                "successMessage",
//                "Order placed successfully!!!");
        if("COD".equals(paymentMethod)){
            return "redirect:/buyer/orders-success/" + order.getOrderId();
        }
        return "redirect:/buyer/payment/" + order.getOrderId();
    }

    // SEE ALL ORDER'S OF BUYER
    @GetMapping("/orders")
    public String viewOrders(Authentication authentication, Model model) {

        String buyerEmail = authentication.getName();

        List<Order> orders = orderService.getOrdersByBuyer(buyerEmail);

        Map<Long, String> paymentStatusMap = new HashMap<>();

        if (orders != null && !orders.isEmpty()) {

            List<Long> orderIds = orders.stream()
                    .map(Order::getOrderId)
                    .toList();

            List<Payment> payments = paymentRepository.findByOrder_OrderIdIn(orderIds);

            if (payments != null) {
                paymentStatusMap = payments.stream()
                        .collect(Collectors.toMap(
                                p -> p.getOrder().getOrderId(),
                                Payment::getPaymentStatus
                        ));
            }
        }

        model.addAttribute("orders", orders);
        model.addAttribute("buyerEmail", buyerEmail);
        model.addAttribute("paymentStatusMap", paymentStatusMap); // ALWAYS NON-NULL

        return "buyer/orders";
    }

    @PostMapping("/review")
    public String submitReview(Authentication authentication,
                               @RequestParam Long orderId,
                               @RequestParam Long productId,
                               @RequestParam Integer rating,
                               @RequestParam String comment,
                               RedirectAttributes redirectAttributes) {

        try {
            String userEmail = authentication.getName();

            // 🔥 THIS CALLS YOUR EXISTING SERVICE (NO CHANGE NEEDED THERE)
            reviewService.addReview(
                    orderId,
                    productId,
                    rating,
                    comment,
                    userEmail
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Review submitted successfully!"
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        }

        // ⭐ CRITICAL FIX: NEVER return "buyer/review"
        // Always redirect back to orders page
        return "redirect:/buyer/orders";
    }

    @GetMapping("/favourites")
    public String viewFavourites(Authentication authentication, Model model) {

        String email = authentication.getName();

        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        List<Favourite> favourites = favouriteRepository.findByBuyer(buyer);

        model.addAttribute("favourites", favourites);

        return "buyer/favourites";
    }

    @PostMapping("/favourite/toggle/{productId}")
    public String toggleFavourite(@PathVariable Long productId,
                                  @RequestParam(defaultValue = "products") String redirectTo,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {

        String email = authentication.getName();

        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Product product = productService.getProductById(productId);

        Optional<Favourite> existing =
                favouriteRepository.findByBuyerAndProduct(buyer, product);

        if (existing.isPresent()) {

            favouriteRepository.delete(existing.get());

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Removed from favourites ❤️"
            );

        } else {

            Favourite favourite = new Favourite();
            favourite.setBuyer(buyer);
            favourite.setProduct(product);
            favourite.setAddedAt(LocalDateTime.now());
            favouriteRepository.save(favourite);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Added to favourites ❤️"
            );
        }

        if ("favourites".equals(redirectTo)) {
            return "redirect:/buyer/favourites";
        }

        return "redirect:/buyer/products";
    }
    // ⚡ BUY NOW - Direct checkout flow (SAFE - uses existing cart logic)
    @GetMapping("/buy-now/{id}")
    public String buyNow(@PathVariable Long id,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

        String buyerEmail = authentication.getName();

        // Add product to cart (reuse existing logic)
        cartService.addToCart(id, buyerEmail);

        // Optional success message
        redirectAttributes.addFlashAttribute("successMessage", "Proceeding to checkout...");

        // Directly go to checkout page (NOT products page)
        return "redirect:/buyer/cart/checkout";
    }
    // 🔔 BUYER NOTIFICATIONS PAGE
    @GetMapping("/notifications")
    public String viewBuyerNotifications(Authentication authentication, Model model) {

        String email = authentication.getName();

        List<Notification> notifications =
                notificationService.getUserNotifications(email);

        model.addAttribute("notifications", notifications);

        return "buyer/notifications";
    }
    @GetMapping("/notifications/read/{id}")
    public String markBuyerNotificationAsRead(@PathVariable Long id,
                                              Authentication authentication) {

        // Security check (optional but best practice)
        notificationService.markAsRead(id);

        return "redirect:/buyer/notifications";
    }
    // 🔴 UNREAD COUNT FOR BELL BADGE (AUTO REFRESH)
    @GetMapping("/notifications/unread-count")
    @ResponseBody
    public long getBuyerUnreadCount(Authentication authentication) {

        String email = authentication.getName();

        return notificationService.getUnreadCount(email);
    }
    @GetMapping("/orders-success/{orderId}")
    public String orderSuccess(@PathVariable Long orderId, Model model){

        Order order = orderRepository.findById(orderId).orElseThrow();
        Payment payment = paymentRepository.findByOrder_OrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("payment",payment);
        return "buyer/orders-success";
    }
    @GetMapping("/notifications/delete/{id}")
    public String deleteNotification(@PathVariable Long id,
                                     Authentication authentication) {

        String email = authentication.getName();

        notificationService.deleteNotification(id, email);

        return "redirect:/buyer/notifications";
    }


}