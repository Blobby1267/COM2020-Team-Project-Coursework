package com.carbon.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.carbon.model.DataForAnalytics;
import com.carbon.model.Evidence;
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

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);


        List<DataForAnalytics> allEvidence = evidenceRepository.findAcceptedEvidenceByUserId(user.getId());

        allEvidence.sort(Comparator.comparing(DataForAnalytics::getSubmittedAt));

        List<DataForAnalytics> weekEvidence = allEvidence.stream().filter(e -> e.getSubmittedAt().isAfter(sevenDaysAgo)).collect(Collectors.toList());        
        List<DataForAnalytics> monthEvidence = allEvidence.stream().filter(e -> e.getSubmittedAt().isAfter(oneMonthAgo)).collect(Collectors.toList());  
        
        int[] weekPoints = new int[7];
        int[] monthPoints = new int[30];
        List<Integer> allPoints = new ArrayList<Integer>();

        List<Integer> weekPointsPie = new ArrayList<Integer>();
        List<Integer> monthPointsPie = new ArrayList<Integer>();
        List<Integer> allPointsPie = new ArrayList<Integer>();

        List<String> weekTaxPie = new ArrayList<String>();
        List<String> monthTaxPie = new ArrayList<String>();
        List<String> allTaxPie = new ArrayList<String>();

        LocalDate today = LocalDate.now();

        for(DataForAnalytics d : weekEvidence){
            LocalDate submittedDate = d.getSubmittedAt().toLocalDate();
            int dayIndex = Math.abs(((int) ChronoUnit.DAYS.between(submittedDate, today))-6);
            weekPoints[dayIndex] += d.getPoints();
            int p = weekTaxPie.indexOf(d.getTaxonomy());
            if(p == -1){
                weekTaxPie.add(d.getTaxonomy());
                weekPointsPie.add(d.getPoints());
            }
            else{
                weekPointsPie.set(p, weekPointsPie.get(p)+d.getPoints());
            }
        }

        for(DataForAnalytics d : monthEvidence){
            LocalDate submittedDate = d.getSubmittedAt().toLocalDate();
            int dayIndex = Math.abs(((int) ChronoUnit.DAYS.between(submittedDate, today)) - 29);
            monthPoints[dayIndex] += d.getPoints();
            int p = monthTaxPie.indexOf(d.getTaxonomy());
            if(p == -1){
                monthTaxPie.add(d.getTaxonomy());
                monthPointsPie.add(d.getPoints());
            }
            else{
                monthPointsPie.set(p, monthPointsPie.get(p)+d.getPoints());
            }
        }

        Map<YearMonth, Integer> pointsByMonth = new TreeMap<>();
        for(DataForAnalytics d : allEvidence){
            YearMonth month = YearMonth.from(d.getSubmittedAt());
            pointsByMonth.merge(month, d.getPoints(), Integer::sum);
            int p = allTaxPie.indexOf(d.getTaxonomy());
            if(p == -1){
                allTaxPie.add(d.getTaxonomy());
                allPointsPie.add(d.getPoints());
            }
            else{
                allPointsPie.set(p, allPointsPie.get(p)+d.getPoints());
            }
        }
        
        List<String> weekLables = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();
        List<String> allLabels = new ArrayList<>();

        for(int i = 6; i>=0; i--){
            weekLables.add(LocalDate.now().minusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }

        for(int i = 29; i>= 0; i--){
            monthLabels.add(LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("dd MMM")));
        }

        allLabels = pointsByMonth.keySet().stream().map(ym -> ym.format(DateTimeFormatter.ofPattern("MMM yyyy"))).collect(Collectors.toList());

        model.addAttribute("weekPoints", weekPoints);
        model.addAttribute("monthPoints", monthPoints);
        model.addAttribute("allPointsByMonth", pointsByMonth.values().toArray());

        model.addAttribute("weekLabels", weekLables.toArray());
        model.addAttribute("monthLabels", monthLabels.toArray());
        model.addAttribute("allLabels", allLabels.toArray());

        model.addAttribute("weekPointsPie", weekPointsPie.toArray());
        model.addAttribute("monthPointsPie", monthPointsPie.toArray());
        model.addAttribute("allPointsPie", allPointsPie.toArray());

        model.addAttribute("weekTaxPie", weekTaxPie.toArray());
        model.addAttribute("monthTaxPie", monthTaxPie.toArray());
        model.addAttribute("allTaxPie", allTaxPie.toArray());

        return "analytics";
    }
}
