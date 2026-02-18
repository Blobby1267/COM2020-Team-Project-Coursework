package com.carbon.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.carbon.model.User;
import com.carbon.repository.UserRepository;

/**
 * Controller for managing navigation between different views.
 * Routes users to appropriate pages based on their role.
 * For example, moderators see moderation interface, users see submission interface.
 * Works with UserRepository to check user roles from database.
 */
@Controller
public class NavigationController {
    // Repository for fetching user details from database
    private final UserRepository userRepository;

    // Constructor injection for UserRepository
    public NavigationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Routes users to appropriate evidence page based on role.
     * @param authentication - Spring Security authentication context
     * @return "m_evidence" for moderators (review interface), "u_evidence" for users (submission interface)
     * 
     * Moderators see pending evidence submissions to approve/reject.
     * Regular users see form to submit their own evidence photos.
     */
    @GetMapping("/evidence")
    public String evidence(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        // Check user role and route to appropriate evidence page
        return hasModeratorRole(authentication) ? "m_evidence" : "u_evidence";
    }

    /**
     * Routes users to appropriate challenges page based on role.
     * @param authentication - Spring Security authentication context
     * @return "m_challenges" for moderators, redirect to "/tasks" for users
     * 
     * Moderators see challenge management interface to create/edit challenges.
     * Regular users are redirected to /tasks to view and complete challenges.
     */
    @GetMapping("/challenges")
    public String challenges(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        // Route moderators to challenge management, users to task completion page
        return hasModeratorRole(authentication) ? "m_challenges" : "redirect:/tasks";
    }

    /** Displays the analytics page. */
    @GetMapping("/analytics")
    public String analytics() {
        return "analytics";
    }

    /** Displays the groups page. */
    @GetMapping("/groups")
    public String groups() {
        return "groups";
    }

    /** Displays the user profile page. */
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    /** Displays the settings page. */
    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    /** Displays the travel tracking page. */
    @GetMapping("/travel")
    public String travel() {
        return "travel";
    }
    
    /**
     * Helper method to check if authenticated user has moderator or admin privileges.
     * @param authentication - Spring Security authentication context
     * @return true if user has moderator/admin role, false otherwise
     */
    private boolean hasModeratorRole(Authentication authentication) {
        // Fetch user from database to check their role
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null || user.getRole() == null) {
            return false;
        }
        return isModeratorRole(user.getRole());
    }

    /**
     * Helper method to check if a role string indicates moderator/admin privileges.
     * Handles various role naming conventions (with/without ROLE_ prefix).
     * @param role - Role string from database
     * @return true if role indicates moderator or admin privileges
     */
    private boolean isModeratorRole(String role) {
        String normalized = role.trim().toUpperCase();
        // Check for various forms of moderator/admin role names
        return normalized.equals("MODERATOR")
            || normalized.equals("ADMIN")
            || normalized.equals("ROLE_MODERATOR")
            || normalized.equals("ROLE_ADMIN");
    }
}
