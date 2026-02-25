package com.revshop.serviceInterfaces;


public interface ForgotPasswordService {

    boolean verifyEmail(String email);

    String getSecurityQuestion(String email);

    boolean resetPassword(String email, String securityAnswer, String newPassword);
}