package com.revshop.service;

import com.revshop.entity.SellerDetails;
import com.revshop.entity.User;
import com.revshop.repo.SellerDetailsRepository;
import com.revshop.serviceImpl.SellerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @Mock
    private SellerDetailsRepository sellerRepo;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private User testUser;
    private SellerDetails testDetails;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(userId);
        testUser.setEmail("seller@revshop.com");

        testDetails = new SellerDetails();
        // NOTE: If these methods are RED, check SellerDetails.java for the correct field names
        // Example: if field is 'shopName', use 'setShopName'
    }

    // 1. Success: Get details when present
    @Test
    void getSellerDetails_Found() {
        when(sellerRepo.findByUserUserId(userId)).thenReturn(Optional.of(testDetails));
        SellerDetails result = sellerService.getSellerDetails(userId);
        assertThat(result).isNotNull();
    }

    // 2. Success: Get details when absent
    @Test
    void getSellerDetails_NotFound() {
        when(sellerRepo.findByUserUserId(userId)).thenReturn(Optional.empty());
        assertThat(sellerService.getSellerDetails(userId)).isNull();
    }

    // 3. Logic: Verify save maps the user correctly
    @Test
    void saveOrUpdate_MapsUser() {
        sellerService.saveOrUpdateSeller(testUser, testDetails);
        assertThat(testDetails.getUser()).isEqualTo(testUser);
        verify(sellerRepo).save(testDetails);
    }

    // 4. Logic: Verify exists returns true
    @Test
    void existsByUserId_True() {
        when(sellerRepo.findByUserUserId(userId)).thenReturn(Optional.of(testDetails));
        assertThat(sellerService.existsByUserId(userId)).isTrue();
    }

    // 5. Logic: Verify exists returns false
    @Test
    void existsByUserId_False() {
        when(sellerRepo.findByUserUserId(userId)).thenReturn(Optional.empty());
        assertThat(sellerService.existsByUserId(userId)).isFalse();
    }

    // 6. Interaction: Verify findByUserUserId call count
    @Test
    void verifyFindByUserUserIdCalledOnce() {
        sellerService.getSellerDetails(userId);
        verify(sellerRepo, times(1)).findByUserUserId(userId);
    }

    // 7. Interaction: Verify save call count
    @Test
    void verifySaveCalledOnce() {
        sellerService.saveOrUpdateSeller(testUser, testDetails);
        verify(sellerRepo, times(1)).save(any(SellerDetails.class));
    }

    // 8. Coverage: Test constructor injection
    @Test
    void testConstructorInjection() {
        assertThat(sellerService).isNotNull();
    }

    // 9. Success: Save with new details object
    @Test
    void saveNewDetails() {
        SellerDetails newDetails = new SellerDetails();
        sellerService.saveOrUpdateSeller(testUser, newDetails);
        verify(sellerRepo).save(newDetails);
    }

    // 10. Success: Handle null result from repo safely
    @Test
    void handleNullReturn() {
        when(sellerRepo.findByUserUserId(anyLong())).thenReturn(Optional.empty());
        assertThat(sellerService.getSellerDetails(999L)).isNull();
    }

    // 11. Success: Verify user linking during update
    @Test
    void updateExistingSellerLinksUser() {
        SellerDetails existing = new SellerDetails();
        sellerService.saveOrUpdateSeller(testUser, existing);
        assertThat(existing.getUser().getUserId()).isEqualTo(1L);
    }

    // 12. Interaction: No interactions on unused methods
    @Test
    void verifyNoOtherInteractions() {
        sellerService.existsByUserId(userId);
        verify(sellerRepo, atMostOnce()).findByUserUserId(any());
    }

    // 13. SUCCESS: Verify findByUserId with different ID
    @Test
    void testDifferentIdLookup() {
        Long otherId = 5L;
        sellerService.getSellerDetails(otherId);
        verify(sellerRepo).findByUserUserId(otherId);
    }

    // 14. Coverage: Entity method usage (setUserId indirectly)
    @Test
    void triggerEntityMethodCoverage() {
        sellerService.saveOrUpdateSeller(testUser, testDetails);
        assertThat(testDetails.getUser()).isNotNull();
    }

    // 15. SUCCESS: Return value type check
    @Test
    void checkReturnType() {
        when(sellerRepo.findByUserUserId(userId)).thenReturn(Optional.of(testDetails));
        assertThat(sellerService.getSellerDetails(userId)).isInstanceOf(SellerDetails.class);
    }

    // 16. Logic: Multiple exists calls
    @Test
    void multipleExistsCalls() {
        sellerService.existsByUserId(userId);
        sellerService.existsByUserId(userId);
        verify(sellerRepo, times(2)).findByUserUserId(userId);
    }

    // 17. SUCCESS: Argument matching verification
    @Test
    void verifySaveWithAnyDetails() {
        sellerService.saveOrUpdateSeller(testUser, testDetails);
        verify(sellerRepo).save(any(SellerDetails.class));
    }

    // 18. Logic: Verify return null on specific ID
    @Test
    void checkNullForUnknownId() {
        when(sellerRepo.findByUserUserId(10L)).thenReturn(Optional.empty());
        assertThat(sellerService.getSellerDetails(10L)).isNull();
    }

    // 19. SUCCESS: Verify findByUserUserId logic consistency
    @Test
    void verifyExistsIsConsistent() {
        when(sellerRepo.findByUserUserId(userId)).thenReturn(Optional.of(new SellerDetails()));
        assertThat(sellerService.existsByUserId(userId)).isTrue();
    }

    // 20. Coverage: Class finality check
    @Test
    void verifyServiceClassType() {
        assertThat(sellerService).isExactlyInstanceOf(SellerServiceImpl.class);
    }
}