package com.carbon.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.carbon.repository.UserRepository;
import java.util.List;
import com.carbon.model.User;

@Controller
public class LeaderboardController {
    @Autowired
    private UserRepository userRepository;
    
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        List<User> users = userRepository.findAll();
        users.sort((u1, u2) -> Integer.compare(u2.getPoints(), u1.getPoints()));
        List<User> top10Users = users.stream().limit(10).toList();
        model.addAttribute("users", top10Users);
        LOGGER.info("Displaying the leaderboard.");
        return "leaderboard";
    }

    @GetMapping("/leaderboard.html")
    public String redirectToLeaderboard() {
        return "redirect:/leaderboard";
    }

    
}
