package com.carbon;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.model.User;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.BadgeService;
import com.carbon.service.TravelService;

@ExtendWith(MockitoExtension.class)
public class TestTravel {
    @Mock
    UserRepository userRepositoryMock;

    @Mock
    EvidenceRepository evidenceRepositoryMock;

    @Mock
    BadgeService badgeServiceMock;

    @Mock
    MultipartFile photoMock;

    //Injects all the repository mocks above into the travel service
    //Avoids the need of creating a travel service using the constructor
    @InjectMocks
    TravelService travelService;

    @Test
    void TestRegisterTravelUserNotFound() throws IOException{
        
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(null);

        //Check there was an exception thrown
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> travelService.registerTravel("testUser", "testType", 1.00, null));
        verify(userRepositoryMock).findByUsername("testUser");
        
        //Assert the message is correct given the values passed into the method.
        assertEquals("User not found: testUser",exception.getMessage());
    }

    @Test
    void testRegisterTravelDuplicateJourneyToday() throws IOException {
        User user = new User();
        user.setUsername("testUser");
        user.setPoints(10);
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(user);
        when(evidenceRepositoryMock.findTravelStatusesInWindow(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq("Travel Journey:%")))
            .thenReturn(List.of(EvidenceStatus.ACCEPTED));

        Throwable exception = assertThrows(IllegalStateException.class, () -> travelService.registerTravel("testUser", "bike", 5.0, null));
        assertEquals("You have already submitted a journey today. You can submit again tomorrow.", exception.getMessage());
    }

    @Test
    void testRegisterTravelOver20KmWithoutEvidence() throws IOException {
        User user = new User();
        user.setUsername("testUser");
        user.setPoints(10);
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(user);
        when(evidenceRepositoryMock.findTravelStatusesInWindow(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq("Travel Journey:%")))
            .thenReturn(List.of());

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> travelService.registerTravel("testUser", "bike", 21.0, null));
        assertEquals("Evidence photo is required for journeys above 20km.", exception.getMessage());
    }

    @Test
    void testRegisterTravelUnder20KmCreatesAcceptedEvidence() throws IOException {
        User user = new User();
        user.setUsername("testUser");
        user.setPoints(10);
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(user);
        when(evidenceRepositoryMock.findTravelStatusesInWindow(eq(user.getId()), any(LocalDateTime.class), any(LocalDateTime.class), eq("Travel Journey:%")))
            .thenReturn(List.of());
        when(evidenceRepositoryMock.save(any(Evidence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> result = travelService.registerTravel("testUser", "walk", 2.0, null);

        assertEquals(10, result.get("pointsEarned"));
        assertEquals(false, result.get("requiresModeratorApproval"));
        assertEquals(20, result.get("totalPoints"));
        assertTrue(result.containsKey("status"));
        verify(userRepositoryMock).save(user);
        verify(evidenceRepositoryMock).save(any(Evidence.class));
        verify(badgeServiceMock).evaluateAllBadgeMechanisms(any());
    }
}
