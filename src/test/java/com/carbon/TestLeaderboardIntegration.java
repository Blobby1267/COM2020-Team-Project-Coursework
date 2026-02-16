package com.carbon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.carbon.model.LeaderboardEntry;
import com.carbon.model.User;
import com.carbon.repository.LeaderboardRepository;

@DataJpaTest
public class TestLeaderboardIntegration {
    /*
    Check if a new user is correctly displayed in the leader board
    Check if a new user with higher points is placed over any users with lower points
    Check if ID of new leaderboard entry stays the same for each entry when order changed
    Check ID auto increments
    */

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    private LeaderboardEntry leaderboardEntry1;
    private LeaderboardEntry leaderboardEntry2;

    private User user1;
    private User user2;
    
    /*
    Use non mocked leaderboardEntry entity as the getters/setters 
        will be tested in a separate test class BEFORE TestLeaderboardIntegration 
        is tested.
    */
    @BeforeEach
    public void setup(){
        user1 = new User();
        user2 = new User();
        user1.setUsername("user1");
        user2.setUsername("user2");

        leaderboardEntry1 = new LeaderboardEntry(user1);
        leaderboardEntry2 = new LeaderboardEntry(user2);

        leaderboardRepository.save(leaderboardEntry1);
        leaderboardRepository.save(leaderboardEntry2);
    }

    @Test
    public void TestIdAutoIncrement(){
        LeaderboardEntry testEntry1 = leaderboardRepository.getReferenceById(leaderboardEntry1.getId());
        LeaderboardEntry testEntry2 = leaderboardRepository.getReferenceById(leaderboardEntry2.getId());

        assertEquals(1, testEntry1.getId());
        assertEquals(2, testEntry2.getId());
    }

    @AfterEach
    public void tearDown(){
        leaderboardRepository.deleteAll();
        leaderboardRepository.flush();
    }
}
