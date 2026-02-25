package com.revshop.serviceInterfaces;

import com.revshop.entity.Order;

import java.util.List;

public interface OrderService {
    void checkout(String buyerEmail,
                  String fullName,
                  String phone,
                  String addressLine1,
                  String addressLine2,
                  String city,
                  String state,
                  String pincode);

    List<Order> getOrdersByBuyer(String email);

}
