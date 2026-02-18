package com.carbon;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Challenge;
import com.carbon.model.Evidence;
import com.carbon.model.User;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.EvidenceService;

@ExtendWith(MockitoExtension.class)
public class TestEvidence {
    @Mock
    EvidenceRepository evidenceRepositoryMock;

    @Mock
    UserRepository userRepositoryMock;

    @Mock
    ChallengeRepository challengeRepository;

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

        //Create challenge object to return when the method findById() is called on the mock repository inside the service
        Challenge testChallenge = new Challenge();
        when(challengeRepository.findById(anyLong())).thenReturn(Optional.of(testChallenge));

        //Create a mock photo object and mock some values for the setters
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(mockPhoto.getOriginalFilename()).thenReturn("CoolName");
        when(mockPhoto.getSize()).thenReturn((long)10);
        when(mockPhoto.getBytes()).thenReturn(new byte[10]);
        when(mockPhoto.isEmpty()).thenReturn(false);
        when(mockPhoto.getContentType()).thenReturn("image/Test-Content");

        //Make sure that the submit evidence is fully successful
        assertDoesNotThrow(() -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));

        //Argument captor is used to get the value passed into a method. We capture the evidence object saved into the repository
        ArgumentCaptor<Evidence> evidenceCaptor = ArgumentCaptor.forClass(Evidence.class);
        verify(evidenceRepositoryMock,times(1)).save(evidenceCaptor.capture());

        //Check that the captor has the same name as our test user, indicating the correct evidence was saved.
        Evidence testEvidence = evidenceCaptor.getValue();
        assertEquals("testUser", testEvidence.getUser().getUsername());
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
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

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
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

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
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

        //Create mock photo and make getContentType() method return invalid named location (should be "image/")
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(mockPhoto.getContentType()).thenReturn("imag/");

        //Get exception
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");

        //Check exception message
        assertEquals("Only image uploads are allowed.",exception.getMessage());
    }
}
