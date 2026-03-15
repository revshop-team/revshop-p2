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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private final String email = "test@revshop.com";

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail(email);
        testUser.setPassword("rawPassword");
    }

    @Test
    void registerUser_Success() {

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");

        userService.registerUser(testUser);

        assertThat(testUser.getPassword()).isEqualTo("hashed");

        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(testUser);
    }

    @Test
    void registerUser_Throws_EmailExists() {

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(testUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_SetsCreatedAt() {

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");

        userService.registerUser(testUser);

        assertThat(testUser.getCreatedAt()).isNotNull();
    }

    @Test
    void findByEmail_Success() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        User result = userService.findByEmail(email);

        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    void findByEmail_UserNotFound() {

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserById_Success() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
    }

    @Test
    void getUserById_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void verifyPasswordEncoderCalled() {

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");

        userService.registerUser(testUser);

        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void verifySaveCalledOnce() {

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");

        userService.registerUser(testUser);

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void verifyRepositoryCallFlow() {

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("hashed");

        userService.registerUser(testUser);

        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(testUser);
    }
}
