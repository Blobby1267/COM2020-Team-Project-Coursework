package com.carbon;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.model.User;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.EvidenceService;

import net.bytebuddy.asm.Advice.Argument;

@ExtendWith(MockitoExtension.class)
public class TestEvidence {
    @Mock
    EvidenceRepository evidenceRepositoryMock;

    @Mock
    UserRepository userRepositoryMock;

    @InjectMocks
    EvidenceService evidenceService;

    @Test
    void TestSubmitEvidenceSuccessful() throws IOException{
        User testUser = new User();
        testUser.setUsername("testUser");
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(mockPhoto.getOriginalFilename()).thenReturn("CoolName");
        when(mockPhoto.getSize()).thenReturn((long)10);
        when(mockPhoto.getBytes()).thenReturn(new byte[10]);
        when(mockPhoto.isEmpty()).thenReturn(false);
        when(mockPhoto.getContentType()).thenReturn("image/Test-Content");

        
        assertDoesNotThrow(() -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));

        ArgumentCaptor<Evidence> evidenceCaptor = ArgumentCaptor.forClass(Evidence.class);
        verify(evidenceRepositoryMock,times(1)).save(evidenceCaptor.capture());

        Evidence testEvidence = evidenceCaptor.getValue();
        assertEquals("testUser", testEvidence.getUser().getUsername());
    }

    @Test
    void TestSubmitEvidenceUserNotFound() throws IOException{
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(null);

        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");
        assertEquals("User not found: testUser",exception.getMessage());
    }

    @Test
    void TestSubmitEvidencePhotoNotFound(){
        final MultipartFile mockPhoto = mock(MultipartFile.class);
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(null);

        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> evidenceService.submitEvidence("testUser", mockPhoto, "testTitle", 1L));
        verify(userRepositoryMock).findByUsername("testUser");
        assertEquals("User not found: testUser",exception.getMessage());
    }

    @Test
    void TestSubmitEvidenceImageOnlyAllowed(){
        User testUser = new User();

        when(userRepositoryMock.findByUsername("testUser")).thenReturn(testUser);

        
    }



}
