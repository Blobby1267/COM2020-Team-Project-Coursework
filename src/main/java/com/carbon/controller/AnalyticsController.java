package com.carbon.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.carbon.model.DataForAnalytics;
import com.carbon.model.User;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;

@Controller
public class AnalyticsController {
    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @GetMapping("/analytics")
    public String analytics(Authentication auth, Model model){

        User user = userRepository.findByUsername(auth.getName());

        List<DataForAnalytics> individualEvidence = evidenceRepository.findAcceptedEvidenceByUserId(user.getId());
        List<DataForAnalytics> yearEvidence = getEvidenceForUsers(userRepository.findByYearIgnoreCase(user.getYear()));
        List<DataForAnalytics> campusEvidence = getEvidenceForUsers(userRepository.findByCampusIgnoreCase(user.getCampus()));

        Map<String, Object> analyticsDataByScope = new LinkedHashMap<>();
        analyticsDataByScope.put("individual", buildScopeAnalyticsData(individualEvidence));
        analyticsDataByScope.put("year", buildScopeAnalyticsData(yearEvidence));
        analyticsDataByScope.put("campus", buildScopeAnalyticsData(campusEvidence));

        model.addAttribute("analyticsDataByScope", analyticsDataByScope);
        model.addAttribute("currentYear", user.getYear());
        model.addAttribute("currentCampus", user.getCampus());

        return "analytics";
    }

    private List<DataForAnalytics> getEvidenceForUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }

        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        return evidenceRepository.findAcceptedEvidenceByUserIds(userIds);
    }

    private Map<String, Object> buildScopeAnalyticsData(List<DataForAnalytics> evidence) {
        List<DataForAnalytics> sortedEvidence = new ArrayList<>(evidence);
        sortedEvidence.sort(Comparator.comparing(DataForAnalytics::getSubmittedAt));

        Map<String, Object> timeframeData = new LinkedHashMap<>();
        timeframeData.put("weekly", buildWeeklyData(sortedEvidence));
        timeframeData.put("monthly", buildMonthlyData(sortedEvidence));
        timeframeData.put("all", buildAllTimeData(sortedEvidence));

        return timeframeData;
    }

    private Map<String, Object> buildWeeklyData(List<DataForAnalytics> evidence) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        List<String> lineLabels = new ArrayList<>();
        List<Double> linePoints = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = startDate.plusDays(i);
            lineLabels.add(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            linePoints.add(0.0);
        }

        List<DataForAnalytics> filtered = new ArrayList<>();
        for (DataForAnalytics item : evidence) {
            LocalDate submittedDate = item.getSubmittedAt().toLocalDate();
            if (!submittedDate.isBefore(startDate) && !submittedDate.isAfter(today)) {
                filtered.add(item);
                int index = (int) ChronoUnit.DAYS.between(startDate, submittedDate);
                linePoints.set(index, linePoints.get(index) + item.getCarbonSaved());
            }
        }

        return buildChartBundle(lineLabels, linePoints, filtered);
    }

    private Map<String, Object> buildMonthlyData(List<DataForAnalytics> evidence) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(29);

        List<String> lineLabels = new ArrayList<>();
        List<Double> linePoints = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            LocalDate day = startDate.plusDays(i);
            lineLabels.add(day.format(DateTimeFormatter.ofPattern("dd MMM")));
            linePoints.add(0.0);
        }

        List<DataForAnalytics> filtered = new ArrayList<>();
        for (DataForAnalytics item : evidence) {
            LocalDate submittedDate = item.getSubmittedAt().toLocalDate();
            if (!submittedDate.isBefore(startDate) && !submittedDate.isAfter(today)) {
                filtered.add(item);
                int index = (int) ChronoUnit.DAYS.between(startDate, submittedDate);
                linePoints.set(index, linePoints.get(index) + item.getCarbonSaved());
            }
        }

        return buildChartBundle(lineLabels, linePoints, filtered);
    }

    private Map<String, Object> buildAllTimeData(List<DataForAnalytics> evidence) {
        Map<YearMonth, Double> pointsByMonth = new TreeMap<>();
        for (DataForAnalytics item : evidence) {
            YearMonth month = YearMonth.from(item.getSubmittedAt());
            pointsByMonth.merge(month, item.getCarbonSaved(), Double::sum);
        }

        List<String> lineLabels = pointsByMonth.keySet().stream()
            .map(ym -> ym.format(DateTimeFormatter.ofPattern("MMM yyyy")))
            .collect(Collectors.toList());
        List<Double> linePoints = new ArrayList<>(pointsByMonth.values());

        return buildChartBundle(lineLabels, linePoints, evidence);
    }

    private Map<String, Object> buildChartBundle(List<String> lineLabels, List<Double> linePoints, List<DataForAnalytics> pieEvidence) {
        Map<String, Double> pieByTaxonomy = new LinkedHashMap<>();
        for (DataForAnalytics item : pieEvidence) {
            String taxonomy = item.getTaxonomy();
            if (taxonomy == null || taxonomy.isBlank()) {
                taxonomy = "Uncategorised";
            }
            pieByTaxonomy.merge(taxonomy, item.getCarbonSaved(), Double::sum);
        }

        Map<String, Object> bundle = new LinkedHashMap<>();
        bundle.put("lineLabels", lineLabels);
        bundle.put("linePoints", linePoints);
        bundle.put("pieLabels", new ArrayList<>(pieByTaxonomy.keySet()));
        bundle.put("piePoints", new ArrayList<>(pieByTaxonomy.values()));

        return bundle;
    }
}
