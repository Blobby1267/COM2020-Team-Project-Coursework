package com.carbon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.carbon.model.LeaderboardEntry;

public interface LeaderboardRepository
        extends JpaRepository<LeaderboardEntry, Long> {
}

