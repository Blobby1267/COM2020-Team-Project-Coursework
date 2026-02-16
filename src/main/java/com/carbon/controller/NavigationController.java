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
        return hasModeratorRole(authentication) ? "redirect:/m_evidence.html" : "redirect:/u_evidence.html";
    }

    @GetMapping("/challenges")
    public String challenges(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        return hasModeratorRole(authentication) ? "redirect:/m_challenges.html" : "redirect:/u_challenges.html";
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
