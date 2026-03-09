package com.revshop.controller;

import com.revshop.serviceInterfaces.ForgotPasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForgotPasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ForgotPasswordService forgotPasswordService;

    // --- 1. RENDER PAGE ---
    @Test
    void testForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"));
    }

    // --- 2. VERIFY EMAIL BRANCHES ---

    @Test
    void verifyEmail_Success_ShowsResetForm() throws Exception {
        when(forgotPasswordService.verifyEmail("test@mail.com")).thenReturn(true);
        when(forgotPasswordService.getSecurityQuestion("test@mail.com")).thenReturn("Pet name?");

        mockMvc.perform(post("/verify-email").param("email","test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("showResetForm", true))
                .andExpect(model().attribute("question", "Pet name?"));
    }

    @Test
    void verifyEmail_NotFound_ShowsError() throws Exception {
        // This targets the 'if (!exists)' branch
        when(forgotPasswordService.verifyEmail("wrong@mail.com")).thenReturn(false);

        mockMvc.perform(post("/verify-email").param("email", "wrong@mail.com"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Email not registered!"))
                .andExpect(view().name("forgot-password"));
    }

    @Test
    void verifyEmail_CheckServiceCall() throws Exception {
        mockMvc.perform(post("/verify-email").param("email", "abc@mail.com"));
        verify(forgotPasswordService).verifyEmail("abc@mail.com");
    }

    // --- 3. RESET PASSWORD BRANCHES ---

    @Test
    void resetPassword_Success_ShowsMessage() throws Exception {
        when(forgotPasswordService.resetPassword("a@b.com","dog","123")).thenReturn(true);

        mockMvc.perform(post("/reset-password")
                        .param("email","a@b.com")
                        .param("securityAnswer","dog")
                        .param("newPassword","123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", "Password changed successfully!"));
    }

    @Test
    void resetPassword_WrongAnswer_ShowsError() throws Exception {
        // This targets the 'if (!success)' branch
        when(forgotPasswordService.resetPassword(any(), any(), any())).thenReturn(false);
        when(forgotPasswordService.getSecurityQuestion("a@b.com")).thenReturn("Question?");

        mockMvc.perform(post("/reset-password")
                        .param("email", "a@b.com")
                        .param("securityAnswer", "wrong")
                        .param("newPassword", "123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Incorrect security answer!"))
                .andExpect(model().attribute("showResetForm", true));
    }

    @Test
    void resetPassword_CheckParams() throws Exception {
        mockMvc.perform(post("/reset-password")
                .param("email", "user@mail.com").param("securityAnswer", "blue").param("newPassword", "pass"));
        verify(forgotPasswordService).resetPassword("user@mail.com", "blue", "pass");
    }

    // --- 4. ADDITIONAL COVERAGE CASES ---

    @Test
    void verifyEmail_CheckEmailAttributeExists() throws Exception {
        when(forgotPasswordService.verifyEmail(anyString())).thenReturn(true);
        mockMvc.perform(post("/verify-email").param("email", "user@mail.com"))
                .andExpect(model().attribute("email", "user@mail.com"));
    }

    @Test
    void resetPassword_Failure_RefetchesQuestion() throws Exception {
        when(forgotPasswordService.resetPassword(any(), any(), any())).thenReturn(false);
        mockMvc.perform(post("/reset-password")
                .param("email", "a@b.com").param("securityAnswer", "x").param("newPassword", "y"));
        verify(forgotPasswordService, atLeastOnce()).getSecurityQuestion("a@b.com");
    }

    @Test
    void forgotPasswordPage_HasNoModelDataInitially() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeDoesNotExist("showResetForm"));
    }

    @Test
    void resetPassword_MissingParam_Status400() throws Exception {
        // Testing Spring's default behavior for missing @RequestParams
        mockMvc.perform(post("/reset-password").param("email", "a@b.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyEmail_EmptyEmail_StillCallsService() throws Exception {
        mockMvc.perform(post("/verify-email").param("email", ""));
        verify(forgotPasswordService).verifyEmail("");
    }

    @Test
    void verifyEmail_ViewVerification() throws Exception {
        mockMvc.perform(post("/verify-email").param("email", "any@mail.com"))
                .andExpect(view().name("forgot-password"));
    }
}