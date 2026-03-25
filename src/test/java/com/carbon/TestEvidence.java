package com.carbon;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Challenge;
import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.model.User;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.BadgeService;
import com.carbon.service.EvidenceService;

@ExtendWith(MockitoExtension.class)
public class TestEvidence {
    @Mock
    EvidenceRepository evidenceRepositoryMock;

    @Mock
    UserRepository userRepositoryMock;

    @Mock
    ChallengeRepository challengeRepository;

    @Mock
    BadgeService badgeServiceMock;

    //Injects all the repository mocks above into the evidence service
    //Avoids the need of creating an evidence service using the constructor
    @InjectMocks
    EvidenceService evidenceService;

    @Test
    void TestSubmitEvidenceSuccessful() throws IOException{
        //Create test user to use in the method call
        User testUser = new User();
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);


        File file = new File("src/test/resources/Background.png");
        FileInputStream input = new FileInputStream(file);
        final MockMultipartFile photoFile = new MockMultipartFile(file.getName(), file.getName(), "image/png", input);

        //Create challenge object to return when the method findById() is called on the mock repository inside the service
        Challenge testChallenge = new Challenge();
        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(testChallenge));
        when(evidenceRepositoryMock.save(any(Evidence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // //Make sure that the submit evidence is fully successful
        Evidence returnedEvidence = evidenceService.submitEvidence("testUser", photoFile, "testTitle", 1L);

        //Argument captor is used to get the value passed into a method. We capture the evidence object saved into the repository
        ArgumentCaptor<Evidence> evidenceCaptor = ArgumentCaptor.forClass(Evidence.class);
        verify(evidenceRepositoryMock).save(evidenceCaptor.capture());

        //Check that the captor has the same name as our test user, indicating the correct evidence was saved.
        assertEquals("testUser", returnedEvidence.getUser().getUsername());
    }

    @Test
    void TestSubmitEvidenceUserNotFound() throws IOException{
        //Mocks photo object so that evidence service doesn't throw an unexpected exception for this unit test
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(null);

        //Check there was an exception thrown
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");
        
        //Assert the message is correct given the values passed into the method.
        assertEquals("User not found: testUser",exception.getMessage());
    }

    @Test
    void TestSubmitEvidencePhotoNotFoundNull(){
        //Create user to avoid unexpected exeception
        User testUser = new User();
        Optional<Challenge> testChallenge = Optional.of(new Challenge());
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);
        when(challengeRepository.findById(1L)).thenReturn(testChallenge);

        //Run the method without passing in a valid photo (null)
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> evidenceService.submitEvidence("testUser", null, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");

        //Check message matches
        assertEquals("Photo is required.",exception.getMessage());
    }

    @Test
    void TestSubmitEvidenceImageOnlyAllowedNullValue(){
        //Create user to avoid unexpected exeception
        User testUser = new User();
        Optional<Challenge> testChallenge = Optional.of(new Challenge());
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);
        when(challengeRepository.findById(1L)).thenReturn(testChallenge);

        //Make mock photo object but mock the method getContentType() to get specific exception
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(mockPhoto.getContentType()).thenReturn(null);

        //Get exception
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");

        //Check exception message
        assertEquals("Only image uploads are allowed.",exception.getMessage());
    }

    @Test
    void TestSubmitEvidenceImageOnlyAllowedContentTypeWrong(){
        //Create user to avoid unexpected exeception
        User testUser = new User();
        Optional<Challenge> testChallenge = Optional.of(new Challenge());
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);
        when(challengeRepository.findById(1L)).thenReturn(testChallenge);

        //Create mock photo and make getContentType() method return invalid named location (should be "image/")
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(mockPhoto.getContentType()).thenReturn("imag/");

        //Get exception
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");

        //Check exception message
        assertEquals("Only image uploads are allowed.",exception.getMessage());
    }

    @Test
    void TestSubmitEvidenceDuplicatePendingReturnsPendingMessage() throws IOException {
        User testUser = new User();
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

        Challenge testChallenge = new Challenge();
        testChallenge.setTitle("testTitle");
        testChallenge.setFrequency("Daily");
        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(testChallenge));
        when(evidenceRepositoryMock.findCompletionStatusesInWindow(any(), anyString(), any(), any()))
            .thenReturn(List.of(EvidenceStatus.PENDING));

        MultipartFile mockPhoto = mock(MultipartFile.class);

        Throwable exception = assertThrows(
            IllegalStateException.class,
            () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L)
        );

        assertEquals("Task waiting to be accepted.", exception.getMessage());
        verify(evidenceRepositoryMock, never()).save(any(Evidence.class));
    }

    @Test
    void TestSubmitEvidenceDuplicateAcceptedReturnsCompletedMessage() throws IOException {
        User testUser = new User();
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

        Challenge testChallenge = new Challenge();
        testChallenge.setTitle("testTitle");
        testChallenge.setFrequency("Weekly");
        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(testChallenge));
        when(evidenceRepositoryMock.findCompletionStatusesInWindow(any(), anyString(), any(), any()))
            .thenReturn(List.of(EvidenceStatus.ACCEPTED));

        MultipartFile mockPhoto = mock(MultipartFile.class);

        Throwable exception = assertThrows(
            IllegalStateException.class,
            () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L)
        );

        assertEquals("Task already completed.", exception.getMessage());
        verify(evidenceRepositoryMock, never()).save(any(Evidence.class));
    }

    @Test
    void TestUpdateEvidenceStatusSuccessful(){
        User user = new User();
        user.setPoints(100);

        Challenge challenge = new Challenge();
        challenge.setPoints(50);

        Evidence evidence = new Evidence();
        evidence.setStatus(EvidenceStatus.PENDING);
        evidence.setUser(user);
        evidence.setChallenge(challenge);

        when(evidenceRepositoryMock.findById(10L)).thenReturn(Optional.of(evidence));
        // Ensure save returns the object to avoid NPE in initializeSummaryFields
        when(evidenceRepositoryMock.save(any(Evidence.class))).thenAnswer(i -> i.getArgument(0));

        Evidence result = evidenceService.updateEvidenceStatus(10L, EvidenceStatus.ACCEPTED, "Good work!");

        assertEquals(EvidenceStatus.ACCEPTED, result.getStatus());
        assertEquals("Good work!", result.getReason());
        assertEquals(150, user.getPoints()); // 100 + 50
        
        verify(userRepositoryMock).save(user);
        verify(badgeServiceMock).evaluateAllBadgeMechanisms(user.getId());
        verify(evidenceRepositoryMock).save(evidence);
    }

    @Test
    void updateEvidenceStatusNoPointsIfAccepted() {
        User user = new User();
        user.setPoints(100);

        Evidence evidence = new Evidence();
        evidence.setStatus(EvidenceStatus.ACCEPTED); // Already accepted
        evidence.setUser(user);
        evidence.setChallenge(new Challenge());

        when(evidenceRepositoryMock.findById(1L)).thenReturn(Optional.of(evidence));
        when(evidenceRepositoryMock.save(any())).thenAnswer(i -> i.getArgument(0));

        evidenceService.updateEvidenceStatus(1L, EvidenceStatus.ACCEPTED, "Still accepted");

        assertEquals(100, user.getPoints()); // Points should remain the same
        verify(userRepositoryMock, never()).save(any(User.class));
    }
}
