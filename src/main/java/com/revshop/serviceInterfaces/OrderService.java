package com.revshop.serviceInterfaces;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.Order;

import java.util.List;

public interface OrderService {
    void checkout(String email,
                  String fullName,
                  String phone,
                  String addressLine1,
                  String addressLine2,
                  String city,
                  String state,
                  String pincode,
                  String paymentMethod);

    List<Order> getOrdersByBuyer(String email);



}
