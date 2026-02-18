package com.carbon;

import com.carbon.model.Challenge;
import com.carbon.repository.ChallengeRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TestChallengeIntegration{

    @Autowired
    private ChallengeRepository challengeRepository;

    // @Mock
    private Challenge challenge1;
    private Challenge challenge2;

    @BeforeEach
    public void setup() throws IllegalAccessException, NoSuchFieldException{

        //Create 2 challenges and save them in the repository
        challenge1 = new Challenge();
        challenge1.setDescription("Test challenge1");
        challenge1.setEndDate(Date.valueOf("2026-02-12"));
        challenge1.setStartDate(Date.valueOf("2026-02-11"));
        challenge1.setFrequency("Daily");
        challenge1.setPoints(400);
        challenge1.setScope("Test Scope1");
        challenge1.setTitle("Test Title1");

        challenge2 = new Challenge();
        challenge2.setDescription("Test challenge2");
        challenge2.setEndDate(Date.valueOf("2026-02-13"));
        challenge2.setStartDate(Date.valueOf("2026-02-11"));
        challenge2.setFrequency("Weekly");
        challenge2.setPoints(300);
        challenge2.setScope("Test Scope2");
        challenge2.setTitle("Test Title2");

        challengeRepository.save(challenge1);
        challengeRepository.save(challenge2);
    }

    @Test
    void TestChallengeSaved(){
        Challenge testChallenge = challengeRepository.getReferenceById(challenge1.getId());
        assertNotNull(testChallenge);
        assertEquals(testChallenge.getDescription(), challenge1.getDescription());
    }

    @Test
    void TestIdAutoIncrement(){

        //Retrive both challenge entities from the repository
        Challenge testChallenge1 = challengeRepository.getReferenceById(challenge1.getId());
        Challenge testChallenge2 = challengeRepository.getReferenceById(challenge2.getId());

        //Make sure IDs are different and consecutive
        assertNotNull(testChallenge1);
        assertNotNull(testChallenge2);
        assertEquals(1,testChallenge1.getId());
        assertEquals(2, testChallenge2.getId());
    }

    @AfterEach
    void tearDown(){
        challengeRepository.deleteAll();
    }
}