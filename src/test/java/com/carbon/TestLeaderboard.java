package com.carbon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.carbon.model.LeaderboardEntry;
import com.carbon.model.User;
import com.carbon.repository.LeaderboardRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.LeaderboardService;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class TestLeaderboard {
    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void TestRebuildLeaderboard3Users() {

        //Create users to test with
        User u1 = new User();
        User u2 = new User();
        User u3 = new User();

        //Assign values to them
        u1.setUsername("Username1");
        u1.setPoints(100);

        u2.setUsername("Username2");
        u2.setPoints(200);

        u3.setUsername("Username3");
        u3.setPoints(300);
        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2, u3));

        //Run the method
        leaderboardService.rebuildLeaderboard();

        verify(leaderboardRepository, times(1)).deleteAll();
        
        //Verify the saveAll method call to be able to get the value of the captor.
        ArgumentCaptor<List<LeaderboardEntry>> captor = ArgumentCaptor.forClass(List.class);
        verify(leaderboardRepository).saveAll(captor.capture());

        //Use the captor to check if all users are still in the leaderboard after sorting
        List<LeaderboardEntry> savedEntries = captor.getValue();
        assertEquals(3, savedEntries.size()); 
    }
}