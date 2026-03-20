package com.carbon.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.carbon.model.DataForAnalytics;

@Service
public class StreakService {

    public int calculateStreak(List<DataForAnalytics> evidence) {
        if (evidence == null || evidence.isEmpty()) {
            return 0;
        }

        Set<LocalDate> completionDates = new HashSet<>();
        for (DataForAnalytics item : evidence) {
            completionDates.add(item.getSubmittedAt().toLocalDate());
        }

        LocalDate currentDay = LocalDate.now();
        if (!completionDates.contains(currentDay)) {
            currentDay = currentDay.minusDays(1);
        }

        int streak = 0;
        while (completionDates.contains(currentDay)) {
            streak++;
            currentDay = currentDay.minusDays(1);
        }

        return streak;
    }
}
