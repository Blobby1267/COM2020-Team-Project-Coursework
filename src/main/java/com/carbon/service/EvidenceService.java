package com.carbon.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.model.User;
import com.carbon.model.Challenge;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.repository.ChallengeRepository;

/**
 * Service layer for evidence submission and moderation business logic.
 * Handles photo uploads, validation, storage, and status updates (accept/reject).
 * Awards points to users when evidence is accepted by moderators.
 * Used by EvidenceController for all evidence-related operations.
 */
@Service
public class EvidenceService {
    // Repository for persisting and retrieving evidence submissions
    private final EvidenceRepository evidenceRepository;
    // Repository for user data 
    private final UserRepository userRepository;
    // Repository for challenge data 
    private final ChallengeRepository challengeRepository;

    // Constructor injection for all required repositories
    public EvidenceService(EvidenceRepository evidenceRepository, UserRepository userRepository, ChallengeRepository challengeRepository) {
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    /**
     * Processes and stores a photo evidence submission from a user.
     * @param username - Username of the submitting user
     * @param photo - Uploaded image file
     * @param taskTitle - Task title for the evidence
     * @param challengeId - ID to link evidence to specific challenge
     * @return Saved Evidence entity with generated ID
     * @throws UsernameNotFoundException if user doesn't exist
     * @throws IllegalArgumentException if photo is invalid or challenge doesn't exist
     * @throws IOException if file reading fails
     * 
     * Storage:
     * - Stores binary photo data as BLOB in database
     * - Stores metadata (filename, content type, size, submission time)
     * - Sets initial status to PENDING for moderator review
     * - Links to User and optionally to Challenge
     */
    public Evidence submitEvidence(String username, MultipartFile photo, String taskTitle, Long challengeId) throws IOException {
        // Validate user exists
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        // Validate photo is provided
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is required.");
        }
        // Validate photo is an image file
        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed.");
        }

        // Create and populate evidence entity
        Evidence evidence = new Evidence();
        evidence.setUser(user);
        evidence.setOriginalFilename(photo.getOriginalFilename());
        evidence.setContentType(contentType);
        evidence.setSizeBytes(photo.getSize());
        evidence.setTaskTitle(taskTitle);
        evidence.setPhoto(photo.getBytes()); // Store binary image data
        evidence.setStatus(EvidenceStatus.PENDING); // Sets status
        evidence.setSubmittedAt(LocalDateTime.now());

        // Link evidence to challenge if challengeId is provided
        if (challengeId != null) {
            Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));
            evidence.setChallenge(challenge);
        }

        return evidenceRepository.save(evidence);
    }

    /**
     * Retrieves all evidence submissions with the specified status.
     * Used by moderators to view pending submissions or review history.
     * @param status - EvidenceStatus to filter by (PENDING, ACCEPTED, or REJECTED)
     * @return List of evidence submissions matching the status
     */
    public List<Evidence> getEvidenceByStatus(EvidenceStatus status) {
        return evidenceRepository.findByStatus(status);
    }

    /**
     * Updates the status of an evidence submission (moderator action).
     * When evidence is ACCEPTED, awards challenge points to the submitting user.
     * @param evidenceId - ID of the evidence to update
     * @param status - New status (ACCEPTED or REJECTED)
     * @return Updated Evidence entity
     * @throws IllegalArgumentException if evidence doesn't exist
     * 
     * Point awarding logic:
     * - If status = ACCEPTED and evidence is linked to a challenge:
     *   → Awards challenge points to user
     *   → Updates user's total points in database
     * - If status = REJECTED or no challenge linked:
     *   → No points awarded
     */
    public Evidence updateEvidenceStatus(Long evidenceId, EvidenceStatus status) {
        // Fetch and validate evidence exists
        Evidence evidence = evidenceRepository.findById(evidenceId)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));
        evidence.setStatus(status);
        
        // Award points to user if evidence is accepted and linked to a challenge
        if (status == EvidenceStatus.ACCEPTED && evidence.getChallenge() != null) {
            User user = evidence.getUser();
            Challenge challenge = evidence.getChallenge();
            int currentPoints = user.getPoints();
            int challengePoints = challenge.getPoints();
            user.setPoints(currentPoints + challengePoints);
            userRepository.save(user); // Update user's total points
        }
        
        return evidenceRepository.save(evidence);
    }

    /**
     * Retrieves a single evidence submission by ID.
     * Used to fetch full evidence details including binary photo data.
     * @param evidenceId - ID of the evidence to retrieve
     * @return Evidence entity with all data including photo bytes
     * @throws IllegalArgumentException if evidence doesn't exist
     */
    public Evidence getEvidence(Long evidenceId) {
        return evidenceRepository.findById(evidenceId)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));
    }
}
