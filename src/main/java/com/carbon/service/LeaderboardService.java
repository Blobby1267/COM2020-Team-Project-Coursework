package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carbon.repository.UserRepository;
import com.carbon.repository.LeaderboardRepository;
import com.carbon.model.User;
import com.carbon.model.LeaderboardEntry;
import java.util.List;


/**
 * Service layer for managing the leaderboard.
 * Provides functionality to rebuild leaderboard from current user standings.
 * Useful for maintaining a cached/optimized view of top users by points.
 * Currently contains rebuild logic; could be extended with scheduled updates.
 */
@Service
public class LeaderboardService {

    // Repository for fetching all users and their points
    private final UserRepository userRepository;
    // Repository for storing leaderboard entries (cached rankings)
    private final LeaderboardRepository leaderboardRepository;

    // Constructor injection for both repositories
    public LeaderboardService(UserRepository userRepository,
                              LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    /**
     * Rebuilds the leaderboard from current user data.
     * @Transactional ensures all database operations complete or none are done.
     * Use cases:
     * - Scheduled job to refresh leaderboard periodically
     * - Manual refresh after bulk point updates
     */
    @Transactional
    public void rebuildLeaderboard() {

        // Fetch all users, sort by points descending, take top 10
        List<User> topUsers = userRepository.findAll().stream()
            .sorted((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()))
            .limit(10)
            .toList();

        // Clear existing leaderboard entries
        leaderboardRepository.deleteAll();

        // Create new leaderboard entries from top users
        List<LeaderboardEntry> entries = topUsers.stream()
            .map(LeaderboardEntry::new) // Converts User to LeaderboardEntry
            .toList();

        // Save all entries to leaderboard table
        leaderboardRepository.saveAll(entries);
    }
}
