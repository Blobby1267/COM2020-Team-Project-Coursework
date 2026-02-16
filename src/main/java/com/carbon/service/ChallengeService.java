package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.model.Challenge;
import com.carbon.model.User;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }
    
    public List<Challenge> getChallengesByFrequency(String frequency) {
        return challengeRepository.findByFrequency(frequency);
    }

    public void completeChallenge(String username, Long challengeId) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));

        // Award points to user
        user.setPoints(user.getPoints() + challenge.getPoints());
        userRepository.save(user);
    }
}