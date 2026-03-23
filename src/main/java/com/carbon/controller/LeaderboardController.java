package com.carbon.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.stream.Collectors;
import com.carbon.repository.UserRepository;
import com.carbon.repository.GroupRepository;
import com.carbon.repository.UserBadgeRepository;
import java.util.List;
import com.carbon.model.User;
import com.carbon.model.UserBadge;

//Controller for displaying the leaderboard page.
@Controller
public class LeaderboardController {
    // Repository for accessing database
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;
    
    // Logger for tracking page views and debugging
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    /**
     * Displays the leaderboard page with top 10 users by points.
     * @param model - Spring Model for passing data to view template
     * @param users - List of users sorted by points
     * @return the name of the Thymeleaf template "leaderboard.html"
     */
    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        // Fetch all users and sort by points
        List<User> users = userRepository.findAll();
        users.sort((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));
        // Limit to top 10 for leaderboard display
        List<User> top10Users = users.stream().limit(10).toList();

        List<GroupLeaderboardRow> top10Groups = groupRepository.findAll().stream()
            .map(group -> new GroupLeaderboardRow(group.getName(), group.getTotalPoints()))
            .sorted((g1, g2) -> Integer.compare(g2.points(), g1.points()))
            .limit(10)
            .toList();

        List<Long> topUserIds = top10Users.stream()
            .map(User::getId)
            .toList();
        Map<Long, String> selectedBadgesByUserId = userBadgeRepository.findByUserIdIn(topUserIds).stream()
            .collect(Collectors.toMap(UserBadge::getUserId, UserBadge::getBadgeName, (existing, replacement) -> existing));

        model.addAttribute("users", top10Users);
        model.addAttribute("groups", top10Groups);
        model.addAttribute("selectedBadgesByUserId", selectedBadgesByUserId);
        LOGGER.info("Displaying the leaderboard.");
        return "leaderboard";
    }

    /**
     * Redirects leaderboard.html URLs to /leaderboard endpoint.
     * Maintains backwards compatibility.
     */
    @GetMapping("/leaderboard.html")
    public String redirectToLeaderboard() {
        return "redirect:/leaderboard";
    }

    public record GroupLeaderboardRow(String name, int points) {
    }
}