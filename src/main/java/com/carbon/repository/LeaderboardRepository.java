package com.carbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbon.model.LeaderboardEntry;

/**
 * Repository interface for LeaderboardEntry entity database operations.
 * Extends JpaRepository which provides CRUD operations for leaderboard entries.
 * Spring Data JPA automatically implements this interface at runtime.
 * Used by LeaderboardService to manage cached leaderboard rankings.
 * The leaderboard can be rebuilt from User data using deleteAll() + saveAll().
 */
public interface LeaderboardRepository
        extends JpaRepository<LeaderboardEntry, Long> {
}

