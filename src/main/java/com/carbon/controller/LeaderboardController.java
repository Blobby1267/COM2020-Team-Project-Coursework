package com.carbon.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.carbon.repository.UserRepository;
import java.util.List;
import com.carbon.model.User;

//Controller for displaying the leaderboard page.
@Controller
public class LeaderboardController {
    // Repository for accessing database
    @Autowired
    private UserRepository userRepository;
    
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
        model.addAttribute("users", top10Users);
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
}