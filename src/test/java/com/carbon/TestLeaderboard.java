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

    @Test
    void TestDisplay10UsersChange(){
        List<User> users = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            User user = new User();
            user.setUsername("user" + i);
            user.setPoints(2);
            users.add(user);
        }
        users.sort(Comparator.comparing(User::getPoints));
        when(userRepository.findAll()).thenReturn(users);

        leaderboardService.rebuildLeaderboard();

        ArgumentCaptor<List<LeaderboardEntry>> captor = ArgumentCaptor.forClass(List.class);
        verify(leaderboardRepository).saveAll(captor.capture());

        //Use the captor to check if all users are still in the leaderboard after sorting
        List<LeaderboardEntry> savedEntries = captor.getValue();
        assertEquals(2,savedEntries.get(0).getPoints());
        assertEquals(10, savedEntries.size()); 
        
        
        User extraUser = new User();
        extraUser.setUsername("NEWUSER");
        extraUser.setPoints(30);

        users.add(extraUser);
        users.sort(Comparator.comparing(User::getPoints));

        leaderboardService.rebuildLeaderboard();

        captor = ArgumentCaptor.forClass(List.class);
        verify(leaderboardRepository,times(2)).saveAll(captor.capture());

        //Use the captor to check if all users are still in the leaderboard after sorting
        savedEntries = captor.getValue();
        assertEquals(30,savedEntries.get(0).getPoints());
        assertEquals(10, savedEntries.size()); 
    }
}