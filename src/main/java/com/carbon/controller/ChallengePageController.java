package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.ChallengeService;
import com.carbon.model.User;

import org.springframework.ui.Model;
import java.util.logging.Logger;
import org.springframework.security.core.Authentication;

@Controller
public class ChallengePageController {
    
    private final ChallengeService challengeService;

    public ChallengePageController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/tasks")
    public String challenge(Authentication auth, Model model) {
        model.addAttribute("dailyChallenges", challengeService.getChallengesByFrequency("Daily"));
        model.addAttribute("weeklyChallenges", challengeService.getChallengesByFrequency("Weekly"));
        model.addAttribute("monthlyChallenges", challengeService.getChallengesByFrequency("Monthly"));
        
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName());
            if (user != null) {
                model.addAttribute("userPoints", user.getPoints());
            }
        }
        
        return "tasks";
    }

    @PostMapping("/api/challenges/complete")
    @ResponseBody
    public ResponseEntity<String> completeChallenge(
        @RequestParam Long challengeId,
        Authentication authentication
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            challengeService.completeChallenge(authentication.getName(), challengeId);
            return ResponseEntity.ok("Challenge completed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/tasks.html")
    public String redirectToTasks() {
        return "redirect:/tasks";
    }
}
