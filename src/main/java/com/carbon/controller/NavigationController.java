package com.carbon.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.carbon.model.User;
import com.carbon.repository.UserRepository;

@Controller
public class NavigationController {
    private final UserRepository userRepository;

    public NavigationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/evidence")
    public String evidence(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return hasModeratorRole(authentication) ? "m_evidence" : "u_evidence";
    }

    @GetMapping("/challenges")
    public String challenges(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return hasModeratorRole(authentication) ? "m_challenges" : "redirect:/tasks";
    }

    @GetMapping("/analytics")
    public String analytics() {
        return "analytics";
    }

    @GetMapping("/groups")
    public String groups() {
        return "groups";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    @GetMapping("/travel")
    public String travel() {
        return "travel";
    }
    
    private boolean hasModeratorRole(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName());
        if (user == null || user.getRole() == null) {
            return false;
        }
        return isModeratorRole(user.getRole());
    }

    private boolean isModeratorRole(String role) {
        String normalized = role.trim().toUpperCase();
        return normalized.equals("MODERATOR")
            || normalized.equals("ADMIN")
            || normalized.equals("ROLE_MODERATOR")
            || normalized.equals("ROLE_ADMIN");
    }
}
