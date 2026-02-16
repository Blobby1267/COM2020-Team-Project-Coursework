package com.carbon;

import com.carbon.controller.ChallengePageController;
import com.carbon.model.Challenge;
import com.carbon.model.LeaderboardEntry;
// import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.ChallengeRepository;
import com.carbon.service.ChallengeService;

// import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import java.util.*;
// import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
// import java.sql.Date;

// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;


@ExtendWith(MockitoExtension.class)
public class TestChallenge {
    @Mock
    ChallengeRepository challengeRepository;

    @InjectMocks
    ChallengeService challengeService;

    @Test
    void TestChallengeGetByFrequencyValid(){
        List<Challenge> challenges = new ArrayList<>();
        Challenge c1 = new Challenge();
        Challenge c2 = new Challenge();


        challenges.add(c1);
        challenges.add(c2);

        when(challengeRepository.findByFrequency("Daily")).thenReturn(challenges);

        List<Challenge> checkChallenge = challengeService.getChallengesByFrequency("Daily");

        verify(challengeRepository, times(1)).findByFrequency("Daily");

        assertEquals(challenges, checkChallenge);
    }


    @Test
    void TestGetId() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        //Java Reflection used
        Field id = challenge.getClass().getDeclaredField("id");
        id.setAccessible(true); //Private field, so make sure we are able to access it
        id.set(challenge,(long)1); //Set name without using setUsername() method
        Assertions.assertEquals(1, challenge.getId());

    }

    @Test
    void TestGetTitle() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        //Java Reflection used
        Field title = challenge.getClass().getDeclaredField("title");
        title.setAccessible(true); //Private field, so make sure we are able to access it
        title.set(challenge,"TestTitle"); //Set name without using setUsername() method
        Assertions.assertEquals("TestTitle", challenge.getTitle());
    }

    @Test
    void TestGetDescription() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        //Java Reflection used
        Field description = challenge.getClass().getDeclaredField("description");
        description.setAccessible(true); //Private field, so make sure we are able to access it
        description.set(challenge,"This challenge is a test"); //Set name without using setUsername() method
        Assertions.assertEquals("This challenge is a test", challenge.getDescription());
    }

    @Test
    void TestGetPoints() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        //Java Reflection used
        Field points = challenge.getClass().getDeclaredField("points");
        points.setAccessible(true); //Private field, so make sure we are able to access it
        points.set(challenge,1); //Set name without using setUsername() method
        Assertions.assertEquals(1, challenge.getPoints());
    }

    @Test
    void TestGetFrequency() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        //Java Reflection used
        Field freq = challenge.getClass().getDeclaredField("frequency");
        freq.setAccessible(true); //Private field, so make sure we are able to access it
        freq.set(challenge,"Daily"); //Set name without using setUsername() method
        Assertions.assertEquals("Daily", challenge.getFrequency());
    }

    @Test
    void TestGetStartDate() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        long now = System.currentTimeMillis();
        java.sql.Date testDate = new java.sql.Date(now);


        //Java Reflection used
        Field startDate = challenge.getClass().getDeclaredField("startDate");
        startDate.setAccessible(true); //Private field, so make sure we are able to access it
        startDate.set(challenge, testDate); //Set name without using setUsername() method
        Assertions.assertEquals(testDate, challenge.getStartDate());
    }

    @Test
    void TestGetEndDate() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();


        long now = System.currentTimeMillis();
        java.sql.Date testDate = new java.sql.Date(now);

        //Java Reflection used
        Field endDate = challenge.getClass().getDeclaredField("endDate");
        endDate.setAccessible(true); //Private field, so make sure we are able to access it
        endDate.set(challenge, testDate); //Set name without using setUsername() method
        Assertions.assertEquals(testDate, challenge.getEndDate());
    }

    @Test
    void TestGetScope() throws IllegalAccessException, NoSuchFieldException {
        Challenge challenge = new Challenge();

        //Java Reflection used
        Field scope = challenge.getClass().getDeclaredField("scope");
        scope.setAccessible(true); //Private field, so make sure we are able to access it
        scope.set(challenge, "Group"); //Set name without using setUsername() method
        Assertions.assertEquals("Group", challenge.getScope());
    }


}
