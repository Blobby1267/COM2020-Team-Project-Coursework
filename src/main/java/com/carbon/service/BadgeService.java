package com.carbon.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.carbon.model.Badge;
import com.carbon.model.EvidenceStatus;
import com.carbon.model.User;
import com.carbon.repository.BadgeRepository;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.GroupRepository;
import com.carbon.repository.UserRepository;

/**
 * Central service for all badge completion mechanisms.
 *
 * Each evaluation method checks eligibility and then inserts a badge row only
 * when the user does not already have the same badge.
 */
@Service
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EvidenceRepository evidenceRepository;
    private final ChallengeRepository challengeRepository;
    private final StreakService streakService;

    public BadgeService(
        BadgeRepository badgeRepository,
        UserRepository userRepository,
        GroupRepository groupRepository,
        EvidenceRepository evidenceRepository,
        ChallengeRepository challengeRepository,
        StreakService streakService
    ) {
        this.badgeRepository = badgeRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.evidenceRepository = evidenceRepository;
        this.challengeRepository = challengeRepository;
        this.streakService = streakService;
    }

    public void evaluateAllBadgeMechanisms(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return;
        }

        int points = user.getPoints();
        int groupCount = Math.toIntExact(groupRepository.countByMembers_Id(userId));
        int streakDays = streakService.calculateStreak(evidenceRepository.findAcceptedEvidenceByUserId(userId));
        int completedDailyTasks = Math.toIntExact(evidenceRepository.countAcceptedByUserIdAndFrequency(userId, "Daily"));
        int completedWeeklyTasks = Math.toIntExact(evidenceRepository.countAcceptedByUserIdAndFrequency(userId, "Weekly"));
        int approvedEvidenceCount = Math.toIntExact(evidenceRepository.countByUser_IdAndStatus(userId, EvidenceStatus.ACCEPTED));
        long completedTaxonomyCount = evidenceRepository.countDistinctAcceptedTaxonomiesByUserId(userId);
        long totalTaxonomyCount = challengeRepository.countDistinctTaxonomies();
        boolean completedEachTaxonomy = totalTaxonomyCount > 0 && completedTaxonomyCount >= totalTaxonomyCount;

        evaluatePointBadges(userId, points);
        evaluateGroupBadges(userId, groupCount);
        evaluateStreakBadges(userId, streakDays);
        evaluateDailyTaskBadges(userId, completedDailyTasks);
        evaluateWeeklyTaskBadges(userId, completedWeeklyTasks);
        evaluateEvidenceBadges(userId, approvedEvidenceCount);
        evaluateVersatileAchiever(userId, completedEachTaxonomy);
        evaluateFootprintMaster(userId);
    }

    public void evaluatePointBadges(Long userId, int points) {
        awardIfEligible(userId, "eco_cadet", points >= 100);
        awardIfEligible(userId, "eco_soldier", points >= 500);
        awardIfEligible(userId, "eco_captain", points >= 1000);
    }

    public void evaluateGroupBadges(Long userId, int groupCount) {
        awardIfEligible(userId, "social_bee", groupCount >= 3);
        awardIfEligible(userId, "queen_social_bee", groupCount >= 10);
    }

    public void evaluateStreakBadges(Long userId, int streakDays) {
        awardIfEligible(userId, "trailwalker", streakDays >= 7);
        awardIfEligible(userId, "hiker", streakDays >= 50);
        awardIfEligible(userId, "pathfinder", streakDays >= 100);
    }

    public void evaluateDailyTaskBadges(Long userId, int completedDailyTasks) {
        awardIfEligible(userId, "new_leaf", completedDailyTasks >= 10);
        awardIfEligible(userId, "growing_leaf", completedDailyTasks >= 50);
        awardIfEligible(userId, "thriving_leaf", completedDailyTasks >= 100);
    }

    public void evaluateWeeklyTaskBadges(Long userId, int completedWeeklyTasks) {
        awardIfEligible(userId, "tree_tender", completedWeeklyTasks >= 5);
        awardIfEligible(userId, "tree_planter", completedWeeklyTasks >= 25);
        awardIfEligible(userId, "tree_gardener", completedWeeklyTasks >= 50);
    }

    public void evaluateEvidenceBadges(Long userId, int approvedEvidenceCount) {
        awardIfEligible(userId, "amateur_ecologist", approvedEvidenceCount >= 5);
        awardIfEligible(userId, "field_ecologist", approvedEvidenceCount >= 10);
        awardIfEligible(userId, "certified_ecologist", approvedEvidenceCount >= 25);
    }

    public void evaluateVersatileAchiever(Long userId, boolean completedEachTaxonomy) {
        awardIfEligible(userId, "versatile_achiever", completedEachTaxonomy);
    }

    public void evaluateFootprintMaster(Long userId) {
        Set<String> completedBadges = getCompletedBadgeNames(userId);
        boolean hasAllRequired = completedBadges.containsAll(List.of(
            "social_bee", "queen_social_bee",
            "versatile_achiever",
            "eco_cadet", "eco_soldier", "eco_captain",
            "trailwalker", "hiker", "pathfinder",
            "new_leaf", "growing_leaf", "thriving_leaf",
            "tree_tender", "tree_planter", "tree_gardener",
            "amateur_ecologist", "field_ecologist", "certified_ecologist"
        ));

        awardIfEligible(userId, "footprint_master", hasAllRequired);
    }

    public Set<String> getCompletedBadgeNames(Long userId) {
        return badgeRepository.findByUserId(userId)
            .stream()
            .map(Badge::getName)
            .collect(Collectors.toSet());
    }

    private void awardIfEligible(Long userId, String badgeName, boolean eligible) {
        if (!eligible) {
            return;
        }

        boolean alreadyAwarded = badgeRepository.existsByUserIdAndName(userId, badgeName);
        if (alreadyAwarded) {
            return;
        }

        Badge badge = new Badge();
        badge.setUserId(userId);
        badge.setName(badgeName);
        badgeRepository.save(badge);
    }
}