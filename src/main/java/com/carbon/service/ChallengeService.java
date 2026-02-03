package com.carbon.service;

import org.springframework.stereotype.Service;
import com.carbon.repository.ChallengeRepository;
import com.carbon.model.Challenge;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }
    
    public List<Challenge> getChallengesByFrequency(String frequency) {
        System.out.println("Fetching challenges with frequency: " + frequency);
        return challengeRepository.findByFrequency(frequency);
    }
}