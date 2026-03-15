package com.revshop.serviceInterfaces;

import com.revshop.entity.Product;
import com.revshop.entity.User;

import java.util.List;

public interface SuggestionService {


    // ✅ viewed suggestion
    List<Product> suggestByView(User user);

    // ✅ ordered suggestion
    List<Product> suggestByOrder(User user);
}
