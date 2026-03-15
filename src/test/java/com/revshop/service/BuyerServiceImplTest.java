package com.revshop.service;

import com.revshop.entity.BuyerDetails;
import com.revshop.entity.User;
import com.revshop.exceptions.UserNotFoundException;
import com.revshop.repo.BuyerDetailsRepository;
import com.revshop.repo.SellerDetailsRepository;
import com.revshop.repo.UserRepository;
import com.revshop.serviceImpl.BuyerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyerServiceImplTest {

    @Mock
    private SellerDetailsRepository sellerDetailsRepository;
    @Mock private BuyerDetailsRepository buyerDetailsRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private BuyerServiceImpl buyerService;

    private User testUser;
    private BuyerDetails testDetails;
    private final String testEmail = "buyer@test.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setEmail(testEmail);

        testDetails = new BuyerDetails();
        testDetails.setFullName("Matloob Prashanth");
    }

    // --- GET DETAILS TESTS ---

    @Test
    void testGetDetails_Success_ExistingDetails() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        BuyerDetails result = buyerService.getBuyerDetailsByEmail(testEmail);
        assertThat(result.getFullName()).isEqualTo("Matloob Prashanth");
    }

    @Test
    void testGetDetails_Success_NoExistingDetails_ReturnsEmpty() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        BuyerDetails result = buyerService.getBuyerDetailsByEmail(testEmail);
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getFullName()).isNull();
    }

    @Test
    void testGetDetails_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> buyerService.getBuyerDetailsByEmail(testEmail))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testGetDetails_VerifyRepositoryInteraction() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        buyerService.getBuyerDetailsByEmail(testEmail);
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(buyerDetailsRepository, times(1)).findById(1L);
    }

    // --- UPDATE DETAILS TESTS ---

    @Test
    void testUpdateDetails_Success_UpdateExisting() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        BuyerDetails newInfo = new BuyerDetails();
        newInfo.setFullName("Updated Name");
        buyerService.updateBuyerDetails(testEmail, newInfo);

        verify(buyerDetailsRepository).save(testDetails);
        assertThat(testDetails.getFullName()).isEqualTo("Updated Name");
    }

    @Test
    void testUpdateDetails_Success_CreateNewIfAbsent() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        buyerService.updateBuyerDetails(testEmail, new BuyerDetails());
        verify(buyerDetailsRepository).save(any(BuyerDetails.class));
    }

    @Test
    void testUpdateDetails_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> buyerService.updateBuyerDetails(testEmail, new BuyerDetails()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testUpdateDetails_VerifyFieldsMappedCorrectly() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        BuyerDetails input = new BuyerDetails();
        input.setFullName("Prashanth");
        input.setPhone("9876543210");
        input.setGender("Male");

        buyerService.updateBuyerDetails(testEmail, input);

        assertThat(testDetails.getFullName()).isEqualTo("Prashanth");
        assertThat(testDetails.getPhone()).isEqualTo("9876543210");
        assertThat(testDetails.getGender()).isEqualTo("Male");
    }

    // --- EDGE CASE & LOGIC TESTS ---

    @Test
    void testUpdateDetails_DoesNotSaveIfUserLookupFails() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        try { buyerService.updateBuyerDetails(testEmail, new BuyerDetails()); } catch (Exception e) {}
        verify(buyerDetailsRepository, never()).save(any());
    }

    @Test
    void testGetDetails_NullEmail_ThrowsException() {
        assertThatThrownBy(() -> buyerService.getBuyerDetailsByEmail(null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void testUpdateDetails_WithEmptyInput_StillSaves() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        buyerService.updateBuyerDetails(testEmail, new BuyerDetails());
        verify(buyerDetailsRepository).save(any());
    }

    @Test
    void testGetDetails_MultipleCalls_HandledCorrectly() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(buyerDetailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        buyerService.getBuyerDetailsByEmail(testEmail);
        buyerService.getBuyerDetailsByEmail(testEmail);

        verify(userRepository, times(2)).findByEmail(testEmail);
    }
}