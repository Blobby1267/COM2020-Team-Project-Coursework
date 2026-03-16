package com.carbon;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.carbon.repository.UserRepository;
import com.carbon.service.TravelService;

@ExtendWith(MockitoExtension.class)
public class TestTravel {
    @Mock
    UserRepository userRepositoryMock;

    //Injects all the repository mocks above into the travel service
    //Avoids the need of creating a travel service using the constructor
    @InjectMocks
    TravelService travelService;

    @Test
    void TestRegisterTravelUserNotFound() throws IOException{
        
        when(userRepositoryMock.findByUsername("testUser")).thenReturn(null);

        //Check there was an exception thrown
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> travelService.registerTravel("testUser", "testType", 1.00));
        verify(userRepositoryMock).findByUsername("testUser");
        
        //Assert the message is correct given the values passed into the method.
        assertEquals("User not found: testUser",exception.getMessage());
    }
}
