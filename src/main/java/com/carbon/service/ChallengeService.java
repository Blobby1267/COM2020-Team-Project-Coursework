package com.carbon.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.model.Challenge;
import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
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
    // Repository for storing automatic evidence records for no-evidence challenges
    private final EvidenceRepository evidenceRepository;
    // Repository for accessing and updating user data
    private final UserRepository userRepository;

    // Constructor injection for repositories (preferred over field injection)
    public ChallengeService(ChallengeRepository challengeRepository, EvidenceRepository evidenceRepository, UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.evidenceRepository = evidenceRepository;
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
     * @return Updated user points total after completion
     * @throws UsernameNotFoundException if user doesn't exist
     * @throws IllegalArgumentException if challenge doesn't exist
     */
    @Transactional
    public int completeChallenge(String username, Long challengeId) {
        // Fetch and validate user exists
        User user = userRepository.findByUsername(username);
        if (user == null) { 
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Fetch and validate challenge exists
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));

        Evidence evidence = new Evidence();
        evidence.setUser(user);
        evidence.setChallenge(challenge);
        evidence.setOriginalFilename("");
        evidence.setContentType("application/octet-stream");
        evidence.setSizeBytes(0);
        evidence.setTaskTitle(challenge.getTitle());
        evidence.setPhoto(new byte[0]);
        evidence.setStatus(EvidenceStatus.AUTO_ACCEPTED);
        evidence.setSubmittedAt(LocalDateTime.now());
        evidenceRepository.save(evidence);

        // Award points to user and save updated total
        int updatedPoints = user.getPoints() + challenge.getPoints();
        user.setPoints(updatedPoints);
        userRepository.save(user);
        return updatedPoints;
    }
}