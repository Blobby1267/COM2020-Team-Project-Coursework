package com.carbon;

import com.carbon.model.DataForAnalytics;
import com.carbon.service.StreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestStreak {

    private StreakService streakService;

    @BeforeEach
    void setUp() {
        streakService = new StreakService();
    }

    @Test
    void calculateStreakNull() {
        assertEquals(0, streakService.calculateStreak(null));
    }

    @Test
    void calculateStreakEmptyEvidence() {
        assertEquals(0, streakService.calculateStreak(Collections.emptyList()));
    }

    @Test
    void calculateStreakNoActivityForTwoDays() {
        // Last submission was 2 days ago — streak is broken
        List<DataForAnalytics> evidence = List.of(
                mockItem(LocalDate.now().minusDays(2))
        );
        assertEquals(0, streakService.calculateStreak(evidence));
    }

    @Test
    void calculateStreak_onlyToday_returnsOne() {
        List<DataForAnalytics> evidence = List.of(
                mockItem(LocalDate.now())
        );
        assertEquals(1, streakService.calculateStreak(evidence));
    }

    @Test
    void calculateStreak_onlyYesterday_returnsOne() {
        // No submission today, but yesterday counts as the start of the streak
        List<DataForAnalytics> evidence = List.of(
                mockItem(LocalDate.now().minusDays(1))
        );
        assertEquals(1, streakService.calculateStreak(evidence));
    }

    private DataForAnalytics mockItem(LocalDate date) {
        return mockItem(date, 9); // default to 9am
    }

    private DataForAnalytics mockItem(LocalDate date, int hour) {
        DataForAnalytics item = mock(DataForAnalytics.class);
        when(item.getSubmittedAt()).thenReturn(LocalDateTime.of(date, LocalTime.of(hour, 0)));
        return item;
    }
}