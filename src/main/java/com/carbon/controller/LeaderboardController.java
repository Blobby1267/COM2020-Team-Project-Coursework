package com.carbon.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

public class LeaderboardController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        // Placeholder for leaderboard data retrieval logic
        LOGGER.info("Displaying the leaderboard.");
        return "leaderboard";
    }

    @GetMapping("/leaderboard.html")
    public String redirectToLeaderboard() {
        return "redirect:/leaderboard";
    }
}
