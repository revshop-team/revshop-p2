package com.revshop.service;

import com.revshop.entity.User;
import com.revshop.exceptions.EmailAlreadyExistsException;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.UserRepository;
import com.revshop.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private final String email = "test@revshop.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail(email);
        testUser.setPassword("rawPassword");
    }

    // --- REGISTER USER TESTS ---

    @Test
    void registerUser_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        userService.registerUser(testUser);

        assertThat(testUser.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void registerUser_Throws_EmailExists() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.registerUser(testUser))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email is already registered");
    }

    @Test
    void registerUser_SetsCreatedAt() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        userService.registerUser(testUser);

        assertThat(testUser.getCreatedAt()).isNotNull();
    }

    // --- FIND BY EMAIL TESTS ---

    @Test
    void findByEmail_Success() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        User result = userService.findByEmail(email);
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void findByEmail_Throws_UserNotFound() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(UserNotFoundException.class);
    }

    // --- GET USER BY ID TESTS ---

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    void getUserById_Throws_RuntimeException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(RuntimeException.class);
    }

    // --- ADDITIONAL COVERAGE CASES (FOR 100%) ---

    @Test
    void registerUser_VerifyNoSaveOnException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        try { userService.registerUser(testUser); } catch (Exception ignored) {}
        verify(userRepository, never()).save(any());
    }

    @Test
    void testEncoderInteraction() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");
        userService.registerUser(testUser);
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void findByEmail_VerifyCorrectEmailPassed() {
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(testUser));
        userService.findByEmail("other@test.com");
        verify(userRepository).findByEmail("other@test.com");
    }

    @Test
    void testMultipleInvocations_getUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.getUserById(1L);
        userService.getUserById(1L);
        verify(userRepository, times(2)).findById(1L);
    }

    @Test
    void registerUser_VerifySaveInstance() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        userService.registerUser(testUser);
        verify(userRepository).save(argThat(u -> u.getEmail().equals(email)));
    }

    @Test
    void testUserObjectConsistency() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        User found = userService.getUserById(1L);
        assertThat(found).isEqualTo(testUser);
    }

    @Test
    void findByEmail_CheckExceptionMessage() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByEmail(email))
                .hasMessage("User not found: " + email);
    }

    @Test
    void testConstructorMapping() {
        assertThat(userService).isNotNull();
    }

    @Test
    void registerUser_VerifyEncoderCallCount() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        userService.registerUser(testUser);
        verify(passwordEncoder, atLeastOnce()).encode(anyString());
    }

    @Test
    void findByEmail_DoesNotCallFindById() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        userService.findByEmail(email);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUserById_DoesNotCallFindByEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        userService.getUserById(1L);
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void testRegisterUser_LogicFlow() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        userService.registerUser(testUser);
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(testUser);
    }

    @Test
    void checkUserEntityMapping_ThroughService() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        User result = userService.findByEmail(email);
        assertThat(result.getUserId()).isEqualTo(1L);
    }
}