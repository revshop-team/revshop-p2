package com.revshop.serviceInterfaces;

import com.revshop.entity.Product;
import java.util.List;

public interface LowStockService {
    List<Product> getLowStockProducts(String sellerEmail);
}