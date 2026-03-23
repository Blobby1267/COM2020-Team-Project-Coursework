package com.carbon.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.carbon.model.User;
import com.carbon.model.UserBadge;
import com.carbon.repository.BadgeRepository;
import com.carbon.repository.UserBadgeRepository;
import com.carbon.repository.UserRepository;

/**
 * Controller for managing navigation between different views.
 * Routes users to appropriate pages based on their role.
 * For example, moderators see moderation interface, users see submission interface.
 * Uses the authenticated user's authorities so page routing matches API authorization.
 */
@Controller
public class NavigationController {
    /**
     * Routes users to appropriate evidence page based on role.
     * @param authentication - Spring Security authentication context
     * @return "m_evidence" for moderators (review interface), "u_evidence" for users (submission interface)
     * 
     * Moderators see pending evidence submissions to approve/reject.
     * Regular users see form to submit their own evidence photos.
     */

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

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

    /** Displays the user profile page. */


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

    @GetMapping("/badges")
    public String badges(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }
        // Build a set of badge names this user has completed (lowercased for easy comparison).
        // Uses a name-only projection to avoid loading the PostgreSQL image LOB column.
        Set<String> completedBadgeNames = badgeRepository.findNamesByUserId(user.getId())
            .stream()
            .map(name -> name.trim().toLowerCase())
            .collect(Collectors.toSet());
        model.addAttribute("completedBadgeNames", completedBadgeNames);
        String selectedBadgeName = userBadgeRepository.findByUserId(user.getId())
            .map(UserBadge::getBadgeName)
            .orElse("");
        model.addAttribute("selectedBadgeName", selectedBadgeName);
        return "badges";
    }

    @PostMapping("/badges/select")
    public String selectBadge(Authentication authentication, @RequestParam("badgeName") String badgeName) {
        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        String normalizedBadgeName = normalizeBadgeName(badgeName);
        if (normalizedBadgeName.isEmpty()) {
            return "redirect:/badges";
        }

        // Users can only select badges they have already earned.
        if (!badgeRepository.existsByUserIdAndNameIgnoreCase(user.getId(), normalizedBadgeName)) {
            return "redirect:/badges";
        }

        UserBadge selectedBadge = userBadgeRepository.findByUserId(user.getId())
            .orElseGet(UserBadge::new);
        selectedBadge.setUserId(user.getId());
        selectedBadge.setBadgeName(normalizedBadgeName);
        userBadgeRepository.save(selectedBadge);

        return "redirect:/badges";
    }
    
    /**
     * Helper method to check if authenticated user has moderator or admin privileges.
     * @param authentication - Spring Security authentication context
     * @return true if user has moderator/admin role, false otherwise
     */
    private boolean hasModeratorRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(this::isModeratorRole);
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

    private String normalizeBadgeName(String badgeName) {
        if (badgeName == null) {
            return "";
        }
        return badgeName.trim().toLowerCase();
    }
}
