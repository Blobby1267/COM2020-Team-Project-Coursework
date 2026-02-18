package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import com.carbon.repository.UserRepository;
import com.carbon.service.ChallengeService;
import com.carbon.model.User;

import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

/**
 * Controller for managing challenge-related pages and operations.
 * Handles displaying challenges to users and processing challenge completions.
 */
@Controller
public class ChallengePageController {
    
    // Calls ChallengeService for logic calculations
    private final ChallengeService challengeService;
    public ChallengePageController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    // Repository for retrieving user information
    @Autowired
    private UserRepository userRepository;

    /**
     * Displays the tasks page with all available challenges.
     * @param auth - Spring Security Authentication object containing the logged-in user's details
     * @param model - Spring MVC Model for passing data to the view template
     * @return the name of the Thymeleaf template "tasks.html" to render
     */
    @GetMapping("/tasks") //maps to tasks.html via redirect
    public String challenge(Authentication auth, Model model) {
        // Fetch and add challenges to the model
        model.addAttribute("dailyChallenges", challengeService.getChallengesByFrequency("Daily"));
        model.addAttribute("weeklyChallenges", challengeService.getChallengesByFrequency("Weekly"));
        model.addAttribute("monthlyChallenges", challengeService.getChallengesByFrequency("Monthly"));
        
        // Check if user is logged in
        if (auth != null) {
            User user = userRepository.findByUsername(auth.getName());
            if (user != null) {
                model.addAttribute("userPoints", user.getPoints()); // Add user's current points to the model
            }
        }
        
        return "tasks";
    }

    /**
     * Endpoint for marking a challenge as complete.
     * Called from the frontend when a user completes a challenge.
     * @param challengeId - The ID of the challenge being completed
     * @param authentication - Spring Security Authentication containing the current user
     * @return ResponseEntity with success/error message and appropriate HTTP status
     */
    @PostMapping("/api/challenges/complete")
    @ResponseBody
    public ResponseEntity<String> completeChallenge(
        @RequestParam Long challengeId,
        Authentication authentication
    ) {
        // check if user is logged in
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        try {
            // Delegate logic calculations to service
            challengeService.completeChallenge(authentication.getName(), challengeId);
            return ResponseEntity.ok("Challenge completed successfully");
        } catch (Exception e) {
            // Return error message if something goes wrong (e.g., challenge not found)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    
    // Redirects tasks.html URLs to the modern /tasks endpoint.
    @GetMapping("/tasks.html")
    public String redirectToTasks() {
        return "redirect:/tasks";
    }
}
