package com.revshop.serviceInterfaces;

public interface ChangePasswordService {

    String changePassword(String email,
                          String currentPassword,
                          String newPassword,
                          String confirmPassword);
}
