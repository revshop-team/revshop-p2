package com.revshop.controller;

import com.revshop.entity.User;
import com.revshop.repo.SecurityQuestionRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceInterfaces.CategoryService;
import com.revshop.serviceInterfaces.ProductService;
import com.revshop.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock private SecurityQuestionRepository securityQuestionRepository;
    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private ProductService productService;
    @Mock private CategoryService categoryService;
    @Mock private UserServiceImpl userService;
    @Mock private Model model;
    @Mock private BindingResult bindingResult;
    @Mock private RedirectAttributes redirectAttributes;

    @InjectMocks private AuthController authController;

    @Test
    public void testTestEndpoint() {
        assertEquals("Auth API Working!", authController.test());
    }

    @Test
    public void testHomePage() {
        when(productService.get12Products()).thenReturn(new ArrayList<>());
        when(categoryService.getAllCategories()).thenReturn(new ArrayList<>());
        assertEquals("public-home", authController.home(model));
    }

    @Test
    public void testShowRegisterForm() {
        when(securityQuestionRepository.findAll()).thenReturn(new ArrayList<>());
        assertEquals("register", authController.showRegisterForm(model));
    }

    @Test
    public void testRegisterUserSuccess() {
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(false);
        assertEquals("redirect:/login", authController.register(user, bindingResult, model, redirectAttributes));
        verify(userService).registerUser(user);
    }

    @Test
    public void testRegisterUserValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);
        assertEquals("register", authController.register(new User(), bindingResult, model, redirectAttributes));
    }

    @Test
    public void testRegisterUserDataIntegrityException() {
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("Dup")).when(userService).registerUser(user);
        assertEquals("register", authController.register(user, bindingResult, model, redirectAttributes));
        verify(model).addAttribute("error", "Duplicate value detected");
    }

    @Test
    public void testRegisterUserRuntimeException() {
        User user = new User();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Error")).when(userService).registerUser(user);
        assertEquals("register", authController.register(user, bindingResult, model, redirectAttributes));
    }

    @Test
    public void testShowLoginForm() {
        assertEquals("login", authController.showLoginForm());
    }

    @Test
    public void testAccessDenied() {
        assertEquals("access-denied", authController.accessDenied());
    }
}