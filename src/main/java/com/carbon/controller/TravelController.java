package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.ChallengeService;
import com.carbon.service.TravelService;

import org.springframework.ui.Model;
import java.util.logging.Logger;
import org.springframework.security.core.Authentication;
import java.util.Map;

/**
 * Controller for managing sustainable travel/commute tracking.
 * Users can submit travel information (type and distance) to earn points.
 * Works with TravelService to calculate points based on travel distance.
 */
@Controller
public class TravelController {
    
    // Service layer for travel-related calculations
    private final TravelService travelService;
    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    // Logger for tracking travel submissions
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    /**
     * Handles travel submission from users.
     * Awards points based on distance traveled using sustainable transport.
     * @param travelType - Type of travel 
     * @param distance - Distance traveled in kilometers
     * @param authentication - Spring Security authentication for current user
     * @param redirectAttributes - For passing calculation results to redirected page
     * @return redirect to travel page with calculation results
     */
    @PostMapping("/api/travel/submit")
    public String completeChallenge(
        @RequestParam String travelType,
        @RequestParam double distance,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        // Calculate and award points based on travel distance
        Map<String, Object> result = travelService.registerTravel(authentication.getName(), travelType, distance);
        // Pass calculation details to redirected page for display
        redirectAttributes.addFlashAttribute("calculation", result);
        return "redirect:/travel";
    }
}