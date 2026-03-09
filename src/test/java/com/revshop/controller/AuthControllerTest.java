package com.revshop.controller;

import com.revshop.entity.User;
import com.revshop.repo.SecurityQuestionRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceImpl.UserServiceImpl;
import com.revshop.serviceInterfaces.CategoryService;
import com.revshop.serviceInterfaces.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

// IMPORTANT: Use Hamcrest Matchers for andExpect() logic
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private SecurityQuestionRepository securityQuestionRepository;
    @MockBean private UserRepository userRepository;
    @MockBean private BCryptPasswordEncoder passwordEncoder;
    @MockBean private ProductService productService;
    @MockBean private CategoryService categoryService;
    @MockBean private UserServiceImpl userService;

    // --- HOME PAGE TESTS ---
    @Test void homePage_ReturnsStatusOk() throws Exception { mockMvc.perform(get("/")).andExpect(status().isOk()); }
    @Test void homePage_ReturnsPublicHomeView() throws Exception { mockMvc.perform(get("/")).andExpect(view().name("public-home")); }
    @Test void homePage_AddsProductsToModel() throws Exception { mockMvc.perform(get("/")).andExpect(model().attributeExists("products")); }
    @Test void homePage_AddsCategoriesToModel() throws Exception { mockMvc.perform(get("/")).andExpect(model().attributeExists("categories")); }
    @Test void homePage_CallsProductServiceOnce() throws Exception { mockMvc.perform(get("/")); verify(productService).get12Products(); }
    @Test void homePage_CallsCategoryServiceOnce() throws Exception { mockMvc.perform(get("/")); verify(categoryService).getAllCategories(); }

    // --- REGISTER GET TESTS ---
    @Test void registerForm_ReturnsStatusOk() throws Exception { mockMvc.perform(get("/register")).andExpect(status().isOk()); }
    @Test void registerForm_ReturnsRegisterView() throws Exception { mockMvc.perform(get("/register")).andExpect(view().name("register")); }
    @Test void registerForm_AddsEmptyUserToModel() throws Exception { mockMvc.perform(get("/register")).andExpect(model().attributeExists("user")); }
    @Test void registerForm_AddsQuestionsToModel() throws Exception { mockMvc.perform(get("/register")).andExpect(model().attributeExists("questions")); }
    @Test void registerForm_CallsQuestionRepo() throws Exception { mockMvc.perform(get("/register")); verify(securityQuestionRepository).findAll(); }
    @Test void registerForm_UserAttributeIsInstanceOfUser() throws Exception {
        mockMvc.perform(get("/register")).andExpect(model().attribute("user", instanceOf(User.class)));
    }

    // --- REGISTER POST SUCCESS ---
    @Test void registerUser_Success_RedirectsToLogin() throws Exception { mockMvc.perform(post("/register-user")).andExpect(status().is3xxRedirection()); }
    @Test void registerUser_Success_RedirectUrlIsCorrect() throws Exception { mockMvc.perform(post("/register-user")).andExpect(redirectedUrl("/login")); }
    @Test void registerUser_Success_CallsUserService() throws Exception { mockMvc.perform(post("/register-user")); verify(userService).registerUser(any()); }
    @Test void registerUser_Success_SetsFlashAttribute() throws Exception { mockMvc.perform(post("/register-user")).andExpect(flash().attributeExists("successMessage")); }
    @Test void registerUser_Success_FlashMessageIsCorrect() throws Exception { mockMvc.perform(post("/register-user")).andExpect(flash().attribute("successMessage", containsString("successful"))); }
    @Test void registerUser_Success_VerifyServiceInteraction() throws Exception { mockMvc.perform(post("/register-user")); verify(userService, atLeastOnce()).registerUser(any()); }

    // --- REGISTER POST FAILURE (Branch Coverage) ---
    @Test void registerUser_Failure_ReturnsRegisterView() throws Exception {
        doThrow(new RuntimeException()).when(userService).registerUser(any());
        mockMvc.perform(post("/register-user")).andExpect(view().name("register"));
    }
    @Test void registerUser_Failure_AddsErrorToModel() throws Exception {
        doThrow(new RuntimeException()).when(userService).registerUser(any());
        mockMvc.perform(post("/register-user")).andExpect(model().attribute("error", "Email already registered"));
    }
    @Test void registerUser_Failure_ReloadsQuestions() throws Exception {
        doThrow(new RuntimeException()).when(userService).registerUser(any());
        mockMvc.perform(post("/register-user")).andExpect(model().attributeExists("questions"));
    }
    @Test void registerUser_Failure_StatusIsOk() throws Exception {
        doThrow(new RuntimeException()).when(userService).registerUser(any());
        mockMvc.perform(post("/register-user")).andExpect(status().isOk());
    }
    @Test void registerUser_Failure_PreservesUserInModel() throws Exception {
        doThrow(new RuntimeException()).when(userService).registerUser(any());
        mockMvc.perform(post("/register-user")).andExpect(model().attributeExists("user"));
    }
    @Test void registerUser_Failure_CallsQuestionRepoAgain() throws Exception {
        doThrow(new RuntimeException()).when(userService).registerUser(any());
        mockMvc.perform(post("/register-user")); verify(securityQuestionRepository, atLeastOnce()).findAll();
    }

    // --- LOGIN & ACCESS DENIED ---
    @Test void loginPage_ReturnsStatusOk() throws Exception { mockMvc.perform(get("/login")).andExpect(status().isOk()); }
    @Test void loginPage_ReturnsLoginView() throws Exception { mockMvc.perform(get("/login")).andExpect(view().name("login")); }
    @Test void accessDeniedPage_ReturnsStatusOk() throws Exception { mockMvc.perform(get("/access-denied")).andExpect(status().isOk()); }
    @Test void accessDeniedPage_ReturnsDeniedView() throws Exception { mockMvc.perform(get("/access-denied")).andExpect(view().name("access-denied")); }

    // --- EXTRA LOGIC CHECKS ---
    @Test void homePage_HandlesEmptyCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/")).andExpect(model().attribute("categories", hasSize(0)));
    }
    @Test void loginPage_VerifyViewNameMatch() throws Exception {
        mockMvc.perform(get("/login")).andExpect(view().name(is("login")));
    }
}