package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carbon.repository.UserRepository;
import com.carbon.repository.LeaderboardRepository;
import com.carbon.model.User;
import com.carbon.model.LeaderboardEntry;
import java.util.List;



@Service
public class LeaderboardService {

    private final UserRepository userRepository;
    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(UserRepository userRepository,
                              LeaderboardRepository leaderboardRepository) {
        this.userRepository = userRepository;
        this.leaderboardRepository = leaderboardRepository;
    }

    @Transactional
    public void rebuildLeaderboard() {

        List<User> topUsers = userRepository.findAll().stream()
            .sorted((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()))
            .limit(10)
            .toList();

        leaderboardRepository.deleteAll();

        List<LeaderboardEntry> entries = topUsers.stream()
            .map(LeaderboardEntry::new)
            .toList();

        leaderboardRepository.saveAll(entries);
    }
}
