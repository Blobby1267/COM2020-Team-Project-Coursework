package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.model.Challenge;
import com.carbon.model.User;
import java.util.List;

/**
 * Service layer for challenge-related business logic.
 * Handles retrieving challenges and awarding points when users complete them.
 * Sits between controllers and repositories to encapsulate business rules.
 * Used by ChallengePageController for challenge operations.
 */
@Service
public class ChallengeService {

    // Repository for accessing challenge data from database
    private final ChallengeRepository challengeRepository;
    // Repository for accessing and updating user data
    private final UserRepository userRepository;

    // Constructor injection for repositories (preferred over field injection)
    public ChallengeService(ChallengeRepository challengeRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Retrieves all challenges matching the specified frequency.
     * @param frequency - "Daily", "Weekly", or "Monthly"
     * @return List of challenges with that frequency
     * 
     * Used by controller to organize challenges by frequency for display.
     */
    public List<Challenge> getChallengesByFrequency(String frequency) {
        return challengeRepository.findByFrequency(frequency);
    }

    /**
     * Marks a challenge as completed for a user and awards points.
     * @param username - Username of the user completing the challenge
     * @param challengeId - ID of the challenge being completed
     * @throws UsernameNotFoundException if user doesn't exist
     * @throws IllegalArgumentException if challenge doesn't exist
     */
    public void completeChallenge(String username, Long challengeId) {
        // Fetch and validate user exists
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Fetch and validate challenge exists
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));

        // Award points to user and save updated total
        user.setPoints(user.getPoints() + challenge.getPoints());
        userRepository.save(user);
    }
}