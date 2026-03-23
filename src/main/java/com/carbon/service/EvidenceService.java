package com.carbon.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private record TimeWindow(LocalDateTime start, LocalDateTime end) {}
    private static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp"
    );
    // Repository for persisting and retrieving evidence submissions
    private final EvidenceRepository evidenceRepository;
    // Repository for user data 
    private final UserRepository userRepository;
    // Repository for challenge data 
    private final ChallengeRepository challengeRepository;
    // Badge logic service
    private final BadgeService badgeService;

    // Constructor injection for all required repositories
    public EvidenceService(
        EvidenceRepository evidenceRepository,
        UserRepository userRepository,
        ChallengeRepository challengeRepository,
        BadgeService badgeService
    ) {
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.badgeService = badgeService;
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
        Challenge linkedChallenge = null;
        if (challengeId != null) {
            linkedChallenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));
        }

        String resolvedTitle = linkedChallenge != null ? linkedChallenge.getTitle() : taskTitle;
        String frequency = linkedChallenge != null ? linkedChallenge.getFrequency() : "Daily";
        Optional<EvidenceStatus> existingStatus = getCompletionStatusInCurrentWindow(user.getId(), resolvedTitle, frequency);
        if (existingStatus.isPresent()) {
            throw new IllegalStateException(getRepeatMessage(existingStatus.get(), frequency));
        }

        // Validate photo is provided
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is required.");
        }
        // Validate is an allowed image type.
        String contentType = photo.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only image uploads are allowed.");
        }

        byte[] imageBytes = photo.getBytes();
        validateImageFile(imageBytes);

        // Create and populate evidence entity
        Evidence evidence = new Evidence();
        evidence.setUser(user);
        evidence.setOriginalFilename(photo.getOriginalFilename());
        evidence.setContentType(contentType);
        evidence.setSizeBytes(photo.getSize());
        evidence.setTaskTitle(resolvedTitle);
        evidence.setPhoto(imageBytes); // Store original validated image bytes
        evidence.setStatus(EvidenceStatus.PENDING); // Sets status
        evidence.setSubmittedAt(LocalDateTime.now());

        // Link evidence to challenge if challengeId is provided
        if (challengeId != null) {
            evidence.setChallenge(linkedChallenge);
        }

        return initializeSummaryFields(evidenceRepository.save(evidence));
    }

    private void validateImageFile(byte[] uploadedBytes) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(uploadedBytes));
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Please submit a valid image file.");
        }
    }

    private TimeWindow getCompletionWindow(String frequency) {
        LocalDate today = LocalDate.now();

        if ("Weekly".equalsIgnoreCase(frequency)) {
            LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            return new TimeWindow(weekStart.atStartOfDay(), weekStart.plusWeeks(1).atStartOfDay());
        }

        if ("Monthly".equalsIgnoreCase(frequency)) {
            LocalDate monthStart = today.withDayOfMonth(1);
            return new TimeWindow(monthStart.atStartOfDay(), monthStart.plusMonths(1).atStartOfDay());
        }

        LocalDateTime dayStart = today.atStartOfDay();
        return new TimeWindow(dayStart, dayStart.plusDays(1));
    }

    public Optional<EvidenceStatus> getCompletionStatusInCurrentWindow(Long userId, String taskTitle, String frequency) {
        TimeWindow window = getCompletionWindow(frequency);
        List<EvidenceStatus> statuses = evidenceRepository.findCompletionStatusesInWindow(
            userId,
            taskTitle,
            window.start(),
            window.end()
        );
        if (statuses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(statuses.get(0));
    }

    private String getRepeatMessage(String frequency) {
        if ("Weekly".equalsIgnoreCase(frequency)) {
            return "You have already completed this weekly task this week. Try again next week.";
        }
        if ("Monthly".equalsIgnoreCase(frequency)) {
            return "You have already completed this monthly task this month. Try again next month.";
        }
        return "You have already completed this daily task today. Try again tomorrow.";
    }

    private String getRepeatMessage(EvidenceStatus status, String frequency) {
        if (status == EvidenceStatus.PENDING) {
            return "Task waiting to be accepted.";
        }
        if (status == EvidenceStatus.ACCEPTED) {
            return "Task already completed.";
        }
        return getRepeatMessage(frequency);
    }

    /**
     * Retrieves all evidence submissions with the specified status.
     * Used by moderators to view pending submissions or review history.
     * @param status - EvidenceStatus to filter by (PENDING, ACCEPTED, or REJECTED)
     * @return List of evidence submissions matching the status
     */
    @Transactional(readOnly = true)
    public List<Evidence> getEvidenceByStatus(EvidenceStatus status) {
        return evidenceRepository.findByStatus(status).stream()
            .map(this::initializeSummaryFields)
            .toList();
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
    @Transactional
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

        if (status == EvidenceStatus.ACCEPTED && evidence.getUser() != null) {
            badgeService.evaluateAllBadgeMechanisms(evidence.getUser().getId());
        }
        
        return initializeSummaryFields(evidenceRepository.save(evidence));
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

    private Evidence initializeSummaryFields(Evidence evidence) {
        if (evidence.getUser() != null) {
            evidence.getUser().getUsername();
        }
        return evidence;
    }
}
