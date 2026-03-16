package com.revshop.controller;

import com.revshop.serviceInterfaces.ForgotPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordControllerTest {

    @Mock private ForgotPasswordService forgotPasswordService;
    @Mock private Model model;
    @InjectMocks private ForgotPasswordController controller;

    @Test
    public void testForgotPasswordPage() {
        assertEquals("forgot-password", controller.forgotPasswordPage());
    }

    @Test
    public void testVerifyEmailSuccess() {
        when(forgotPasswordService.verifyEmail("test@test.com")).thenReturn(true);
        when(forgotPasswordService.getSecurityQuestion("test@test.com")).thenReturn("Pet name?");
        assertEquals("forgot-password", controller.verifyEmail("test@test.com", model));
        verify(model).addAttribute("showResetForm", true);
    }

    @Test
    public void testVerifyEmailNotFound() {
        when(forgotPasswordService.verifyEmail("wrong@test.com")).thenReturn(false);
        assertEquals("forgot-password", controller.verifyEmail("wrong@test.com", model));
        verify(model).addAttribute("error", "Email not registered!");
    }

    @Test
    public void testResetPasswordSuccess() {
        when(forgotPasswordService.resetPassword("a@a.com", "ans", "new")).thenReturn(true);
        assertEquals("forgot-password", controller.resetPassword("a@a.com", "ans", "new", model));
        verify(model).addAttribute("success", "Password changed successfully!");
    }

    @Test
    public void testResetPasswordFail() {
        when(forgotPasswordService.resetPassword("a@a.com", "wrong", "new")).thenReturn(false);
        assertEquals("forgot-password", controller.resetPassword("a@a.com", "wrong", "new", model));
        verify(model).addAttribute("error", "Incorrect security answer!");
    }
}