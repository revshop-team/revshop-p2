package com.revshop.serviceInterfaces;

import com.revshop.entity.User;

public interface UserService {

    void registerUser(User user);

    User findByEmail(String email);
}
