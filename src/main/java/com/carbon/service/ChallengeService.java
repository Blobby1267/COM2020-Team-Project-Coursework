package com.carbon.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/**
 * Service layer for challenge-related business logic.
 * Handles retrieving challenges and awarding points when users complete them.
 * Sits between controllers and repositories to encapsulate business rules.
 * Used by ChallengePageController for challenge operations.
 */
@Service
public class ChallengeService {

    private record TimeWindow(LocalDateTime start, LocalDateTime end) {}

    // Repository for accessing challenge data from database
    private final ChallengeRepository challengeRepository;
    // Badge service for evaluating and awarding badges
    private final BadgeService badgeService;
    // Repository for storing automatic evidence records for no-evidence challenges
    private final EvidenceRepository evidenceRepository;
    // Repository for accessing and updating user data
    private final UserRepository userRepository;

    // Constructor injection for repositories (preferred over field injection)
    public ChallengeService(
        ChallengeRepository challengeRepository,
        BadgeService badgeService,
        EvidenceRepository evidenceRepository,
        UserRepository userRepository
    ) {
        this.challengeRepository = challengeRepository;
        this.badgeService = badgeService;
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
     * Returns the set of task titles the user has already completed today (local time).
     * Used to highlight completed tasks on the tasks page.
     */
    public Set<String> getCompletedTaskTitlesToday(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return Set.of();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);
        return new HashSet<>(evidenceRepository.findTodayCompletedTaskTitles(user.getId(), startOfDay, startOfNextDay));
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

        TimeWindow window = getCompletionWindow(challenge.getFrequency());
        if (evidenceRepository.hasEvidenceForTaskInWindow(user.getId(), challenge.getTitle(), window.start(), window.end())) {
            throw new IllegalStateException(getRepeatMessage(challenge.getFrequency()));
        }

        Evidence evidence = new Evidence();
        evidence.setUser(user);
        evidence.setChallenge(challenge);
        evidence.setOriginalFilename("");
        evidence.setContentType("application/octet-stream");
        evidence.setSizeBytes(0);
        evidence.setTaskTitle(challenge.getTitle());
        evidence.setPhoto(new byte[0]);
        evidence.setStatus(EvidenceStatus.ACCEPTED);
        evidence.setSubmittedAt(LocalDateTime.now());
        evidenceRepository.save(evidence);

        // Award points to user and save updated total
        int updatedPoints = user.getPoints() + challenge.getPoints();
        user.setPoints(updatedPoints);
        userRepository.save(user);

        badgeService.evaluateAllBadgeMechanisms(user.getId());
        return updatedPoints;
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

    private String getRepeatMessage(String frequency) {
        if ("Weekly".equalsIgnoreCase(frequency)) {
            return "You have already completed this weekly task this week. Try again next week.";
        }
        if ("Monthly".equalsIgnoreCase(frequency)) {
            return "You have already completed this monthly task this month. Try again next month.";
        }
        return "You have already completed this daily task today. Try again tomorrow.";
    }
}