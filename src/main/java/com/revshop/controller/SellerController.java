package com.revshop.controller;

import com.revshop.entity.*;
import com.revshop.repo.*;
import com.revshop.serviceInterfaces.*;
import org.aspectj.weaver.ast.Not;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final ProductService productService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final SellerService sellerService;
    private final PaymentRepository paymentRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final LowStockService lowStockService;
    private final NotificationService notificationService;
    private final OrderService orderService;
    public SellerController(ProductService productService,
                            UserService userService,
                            CategoryRepository categoryRepository,
                            SellerService sellerService,
                            OrderItemRepository orderItemRepository,
                            PaymentRepository paymentRepository,
                            OrderAddressRepository orderAddressRepository, ReviewRepository reviewRepository, LowStockService lowStockService,
                            NotificationService notificationService,
                            OrderService orderservice) {

        this.productService = productService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.sellerService = sellerService;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.reviewRepository = reviewRepository;
        this.lowStockService = lowStockService;
        this.notificationService=notificationService;
        this.orderService=orderservice;

    }

    // RENDER SELLER'S DASHBOARD
    @GetMapping("/dashboard")
    public String sellerDashboard() {
        return "seller/dashboard";
    }

    // SELLER PRODUCTS
    @GetMapping("/products")
    public String sellerProducts() {
        return "seller/products";
    }

    // SELLER'S SALES
//    @GetMapping("/sales")
//    public String sellerSales() {
//        return "seller/sales";
//    }

    // show add product page
    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        System.out.println("Categories: " + categoryRepository.findAll().size());
        return "seller/add-product";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {

        String email = authentication.getName();  // logged-in email

        User user = userService.findByEmail(email);

        SellerDetails details = sellerService.getSellerDetails(user.getUserId());

        if (details == null) {
            details = new SellerDetails();
        }

        model.addAttribute("sellerDetails", details);

        return "seller/seller-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute SellerDetails details,
                                Authentication authentication,Model model) {



        try {

            String email = authentication.getName();

            User user = userService.findByEmail(email);

            sellerService.saveOrUpdateSeller(user, details);

            return "redirect:/seller/profile?success";

        } catch (Exception e) {

            e.printStackTrace();

            model.addAttribute("error",
                    "Business name / GST / Phone already exists");

            model.addAttribute("sellerDetails", details);

            return "seller/seller-profile";
        }
    }

    // SAVE PRODUCTS (STATIC IMAGE VERSION - FINAL)
    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam(value = "newCategoryName", required = false) String newCategoryName,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        try {
            System.out.println("INSIDE SAVE PRODUCT METHOD");

            // 🔹 Get Logged-in Seller
            String email = authentication.getName();
            User seller = userService.findByEmail(email);

            if (seller == null) {
                model.addAttribute("error", "Seller not found. Please login again.");
                model.addAttribute("categories", categoryRepository.findAll());
                return "seller/add-product";
            }

            // 🔹 FIX: Null category object from form
            if (product.getCategory() != null &&
                    product.getCategory().getCategoryId() == null) {
                product.setCategory(null);
            }

            Category selectedCategory = product.getCategory();

            boolean hasDropdown = selectedCategory != null && selectedCategory.getCategoryId() != null;
            boolean hasManual = newCategoryName != null && !newCategoryName.trim().isEmpty();

            // ❌ BOTH FILLED
            if (hasDropdown && hasManual) {
                model.addAttribute("error", "Select OR enter category, not both.");
                model.addAttribute("categories", categoryRepository.findAll());
                return "seller/add-product";
            }

            // ❌ NONE FILLED
            if (!hasDropdown && !hasManual) {
                model.addAttribute("error", "Please select a category.");
                model.addAttribute("categories", categoryRepository.findAll());
                return "seller/add-product";
            }

            // ✅ MANUAL CATEGORY CREATION
            if (hasManual) {
                Category category = categoryRepository
                        .findByCategoryNameIgnoreCase(newCategoryName.trim())
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setCategoryName(newCategoryName.trim());
                            newCategory.setDescription("Added by seller");
                            return categoryRepository.save(newCategory);
                        });

                product.setCategory(category);
            }

            // 🔥 =========================
            // 🔥 BACKEND VALIDATIONS (VERY IMPORTANT)
            // 🔥 =========================

            // Price Validation
            if (product.getMrp() != null && product.getSellingPrice() != null) {
                if (product.getSellingPrice() > product.getMrp()) {
                    model.addAttribute("error", "Selling price cannot be greater than MRP.");
                    model.addAttribute("categories", categoryRepository.findAll());
                    return "seller/add-product";
                }
            }

            // Discount validation
            if (product.getDiscount() != null) {
                if (product.getDiscount() < 0 || product.getDiscount() > 100) {
                    model.addAttribute("error", "Discount must be between 0 and 100.");
                    model.addAttribute("categories", categoryRepository.findAll());
                    return "seller/add-product";
                }
            }

            // Stock Validation
            if (product.getStock() != null && product.getStock() < 0) {
                model.addAttribute("error", "Stock cannot be negative.");
                model.addAttribute("categories", categoryRepository.findAll());
                return "seller/add-product";
            }

            // Threshold Validation
            if (product.getStockThreshold() != null && product.getStock() != null) {
                if (product.getStockThreshold() > product.getStock()) {
                    model.addAttribute("error", "Stock threshold cannot be greater than stock.");
                    model.addAttribute("categories", categoryRepository.findAll());
                    return "seller/add-product";
                }
            }

            // 🔥 REQUIRED FIELDS (ORACLE SAFE)
            product.setSeller(seller);
            product.setCreatedAt(LocalDateTime.now());
            product.setIsActive(1);

            // 🔹 STATIC IMAGE SAFETY (since you removed file upload)
            if (product.getImageName() == null || product.getImageName().isBlank()) {
                product.setImageName("default.png");
            }

            // 🔥 IMPORTANT: Prevent unique constraint / update conflict
            product.setProductId(null);

            // SAVE PRODUCT
            productService.saveProduct(product, seller);

            // 🔥 SUCCESS FLASH MESSAGE (for my-products page)
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Product added successfully!"
            );

            return "redirect:/seller/my-products";

        } catch (Exception e) {
            e.printStackTrace();

            // 🔥 ORACLE / DB ERROR HANDLING (prevents WhiteLabel 500 page)
            model.addAttribute("error", "Failed to save product. Please try again.");
            model.addAttribute("categories", categoryRepository.findAll());
            return "seller/add-product";
        }
    }


    // VIEW SELLER'S PRODUCTS
    @GetMapping("/my-products")
    public String viewProducts(Model model,
                               Authentication authentication) {

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        model.addAttribute("products",
                productService.getProductBySeller(seller)
                        .stream()
                        .filter(p -> p.getIsActive() == 1)
                        .toList());

        return "seller/my-products";
    }

    // Show edit page
    @GetMapping("/edit-product/{id}")
    public String showEditProduct(@PathVariable Long id,RedirectAttributes  redirectAttributes, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/seller/my-products?error";
        }
        model.addAttribute("product", product);

        return "seller/edit-product";
    }


    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String productName,
                                @RequestParam String description,
                                @RequestParam String manufacturer,
                                @RequestParam Double mrp,
                                @RequestParam Double discount,
                                @RequestParam Double sellingPrice,
                                @RequestParam Integer stock,
                                @RequestParam Integer stockThreshold,
                                RedirectAttributes redirectAttributes) {  // 🔥 ADD THIS

        // fetch existing product
        Product product = productService.getProductById(id);

        if (product == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Product not found!"
            );
            return "redirect:/seller/my-products";
        }

        // update only editable fields
        product.setProductName(productName);
        product.setDescription(description);
        product.setManufacturer(manufacturer);
        product.setMrp(mrp);
        product.setDiscount(discount);
        product.setSellingPrice(sellingPrice);
        product.setStock(stock);
        product.setStockThreshold(stockThreshold);

        productService.saveOrUpdateProduct(product);

        // ✅ SUCCESS MESSAGE (THIS WAS MISSING)
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Product details updated successfully!"
        );

        return "redirect:/seller/my-products";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        productService.deleteProductById(id);

        // ✅ SUCCESS FLASH MESSAGE
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Product deleted successfully!"
        );

        return "redirect:/seller/my-products";
    }

    // VIEW ORDERS FOR SELLER PRODUCTS (NEW - CRITICAL)
    @GetMapping("/orders")
    public String sellerOrders(@RequestParam(value = "status", required = false) String status,
                               Authentication authentication,
                               Model model) {

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        // 1. Get all order items of this seller
        List<OrderItem> orderItems = orderItemRepository.findBySeller(seller);

        // 🔥 2. FILTER LOGIC (All / Pending / Delivered)
        if (status != null && !status.isEmpty()) {
            orderItems = orderItems.stream()
                    .filter(item -> item.getOrder() != null
                            && status.equalsIgnoreCase(item.getOrder().getStatus()))
                    .toList();
        }

        // 3. Extract unique orders (for payment & address mapping)
        List<Order> orders = orderItems.stream()
                .map(OrderItem::getOrder)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 4. Fetch payments map (OrderId -> Payment)
        Map<Long, Payment> paymentMap = new HashMap<>();
        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream()
                    .map(Order::getOrderId)
                    .toList();

            List<Payment> payments = paymentRepository.findByOrder_OrderIdIn(orderIds);
            for (Payment payment : payments) {
                paymentMap.put(payment.getOrder().getOrderId(), payment);
            }
        }

        // 5. Fetch address map (OrderId -> Address)
        Map<Long, OrderAddress> addressMap = new HashMap<>();
        if (!orders.isEmpty()) {
            for (Order order : orders) {
                OrderAddress address = orderAddressRepository.findByOrder(order);
                if (address != null) {
                    addressMap.put(order.getOrderId(), address);
                }
            }
        }

        // 6. Send data to UI
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("paymentMap", paymentMap);
        model.addAttribute("addressMap", addressMap);
        model.addAttribute("selectedStatus", status); // for active tab highlight

        return "seller/orders";
    }
    // ⭐ SELLER REVIEWS & RATINGS DASHBOARD
    @GetMapping("/reviews")
    public String sellerReviews(Authentication authentication, Model model) {

        String email = authentication.getName();
        User seller = userService.findByEmail(email);

        List<Review> reviews = reviewRepository.findReviewsForSellerProducts(seller);

        // 🔥 GROUP BY PRODUCT
        Map<Product, List<Review>> productReviewsMap = new LinkedHashMap<>();
        Map<Long, Double> avgRatingMap = new HashMap<>();
        Map<Long, Long> reviewCountMap = new HashMap<>();

        for (Review review : reviews) {
            Product product = review.getProduct();
            Long productId = product.getProductId();

            // Group reviews
            productReviewsMap
                    .computeIfAbsent(product, k -> new ArrayList<>())
                    .add(review);

            // Add avg + count only once per product
            avgRatingMap.putIfAbsent(productId,
                    reviewRepository.getAverageRatingByProductId(productId));

            reviewCountMap.putIfAbsent(productId,
                    reviewRepository.countByProduct_ProductId(productId));
        }

        model.addAttribute("productReviewsMap", productReviewsMap);
        model.addAttribute("avgRatingMap", avgRatingMap);
        model.addAttribute("reviewCountMap", reviewCountMap);

        return "seller/reviews";
    }
    @GetMapping("/low-stock")
    public String viewLowStock(Authentication authentication, Model model) {

        String email = authentication.getName();

        List<Product> lowStockProducts =
                lowStockService.getLowStockProducts(email);

        model.addAttribute("lowStockProducts", lowStockProducts);

        return "seller/low-stock";
    }
    // 🔔 Get seller notifications page
    @GetMapping("/notifications")
    public String viewNotifications(Authentication authentication, Model model) {

        String email = authentication.getName();

        List<Notification> notifications =
                notificationService.getUserNotifications(email);

        model.addAttribute("notifications", notifications);

        return "seller/notifications";
    }

    // 🔴 Get unread count (for bell icon)
    @GetMapping("/notifications/unread-count")
    @ResponseBody
    public long getUnreadCount(Authentication authentication) {

        String email = authentication.getName();
        return notificationService.getUnreadCount(email);
    }

    // ✔ Mark notification as read
    @GetMapping("/notifications/read/{id}")
    public String markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id);
        return "redirect:/seller/notifications";
    }
    @PostMapping("/orders/deliver/{orderId}")
    public String markOrderDelivered(@PathVariable Long orderId,
                                     RedirectAttributes redirectAttributes) {

        orderService.markAsDelivered(orderId);



        return "redirect:/seller/orders";
    }
    @GetMapping("/sales")
    public String sellerAnalytics(Authentication authentication, Model model) {

        String email = authentication.getName();
        User seller = userService.findByEmail(email);
        System.out.println("LOGGED SELLER ID: " + seller.getUserId());
        // 🔥 FETCH ALL ORDER ITEMS (Reliable)
        List<OrderItem> allItems = orderItemRepository.findAll();
        System.out.println("TOTAL ITEMS IN DB: " + allItems.size());
        // 🔥 FILTER BY SELLER ID (100% MATCH WITH DB seller_id column)
        List<OrderItem> orderItems = allItems.stream()
                .filter(item -> item.getSeller() != null
                        && item.getSeller().getUserId().equals(seller.getUserId()))
                .toList();
        System.out.println("SELLER FILTERED ITEMS: " + orderItems.size());

        long totalOrders = 0;
        long pendingOrders = 0;
        long deliveredOrders = 0;
        double totalRevenue = 0.0;
        double averageOrderValue = 0.0;

        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        Map<String, Integer> productSalesMap = new HashMap<>();

        // Unique orders for correct counting
        List<Order> uniqueOrders = orderItems.stream()
                .map(OrderItem::getOrder)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        totalOrders = uniqueOrders.size();

        for (OrderItem item : orderItems) {
            Order order = item.getOrder();
            if (order == null) continue;

            String status = order.getStatus();

            if ("PLACED".equalsIgnoreCase(status)) {
                pendingOrders++;
            }

            if ("DELIVERED".equalsIgnoreCase(status)) {
                deliveredOrders++;

                double itemTotal = item.getPrice() * item.getQuantity();
                totalRevenue += itemTotal;

                // Monthly revenue (for chart)
                if (order.getOrderDate() != null) {
                    String month = order.getOrderDate().getMonth().toString();
                    monthlyRevenue.put(month,
                            monthlyRevenue.getOrDefault(month, 0.0) + itemTotal);
                }
            }

            // Top selling product
            String productName = item.getProduct().getProductName();
            productSalesMap.put(productName,
                    productSalesMap.getOrDefault(productName, 0) + item.getQuantity());
        }

        if (deliveredOrders > 0) {
            averageOrderValue = totalRevenue / deliveredOrders;
        }

        // Find top product
        String topProduct = "No Sales Yet";
        int maxQty = 0;
        for (Map.Entry<String, Integer> entry : productSalesMap.entrySet()) {
            if (entry.getValue() > maxQty) {
                maxQty = entry.getValue();
                topProduct = entry.getKey();
            }
        }

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("averageOrderValue", averageOrderValue);
        model.addAttribute("topProduct", topProduct);
        model.addAttribute("months", new ArrayList<>(monthlyRevenue.keySet()));
        model.addAttribute("revenues", new ArrayList<>(monthlyRevenue.values()));

        return "seller/sales";
    }
    @GetMapping("/reviews/product/{id}")
    public String viewAllReviewsForProduct(@PathVariable Long id, Model model) {

        List<Review> reviews =
                reviewRepository.findByProduct_ProductIdOrderByReviewDateDesc(id);

        if (reviews.isEmpty()) {
            model.addAttribute("reviews", reviews);
            model.addAttribute("avgRating", 0.0);
            model.addAttribute("totalReviews", 0);
            model.addAttribute("ratingCount", new HashMap<Integer, Long>());
            return "seller/product-reviews";
        }

        Product product = reviews.get(0).getProduct();

        Double avgRating =
                reviewRepository.getAverageRatingByProductId(id);

        if (avgRating == null) avgRating = 0.0;

        long totalReviews =
                reviewRepository.countByProduct_ProductId(id);

        Map<Integer, Long> ratingCount = new HashMap<>();

        for (int i = 1; i <= 5; i++) {

            int rating = i;

            long count = reviews.stream()
                    .filter(r -> r.getRating() == rating)
                    .count();

            ratingCount.put(i, count);
        }

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("ratingCount", ratingCount);

        return "seller/product-reviews";
    }
    @GetMapping("/notifications/clear-all")
    public String clearSellerNotifications(Authentication authentication) {

        String email = authentication.getName();

        notificationService.clearAllNotifications(email);

        return "redirect:/seller/notifications";
    }
}
