package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.service.TravelService;

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
        @RequestParam(value = "photo", required = false) MultipartFile photo,
        Authentication authentication,
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Calculate and award points based on travel distance
            Map<String, Object> result = travelService.registerTravel(authentication.getName(), travelType, distance, photo);
            // Pass calculation details to redirected page for display
            redirectAttributes.addFlashAttribute("calculation", result);
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("travelError", e.getMessage());
        } catch (Exception e) {
            LOGGER.warning("Travel submission failed: " + e.getMessage());
            redirectAttributes.addFlashAttribute("travelError", "Unable to submit travel at the moment. Please try again.");
        }
        return "redirect:/travel";
    }
}