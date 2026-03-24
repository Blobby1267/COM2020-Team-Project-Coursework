package com.carbon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.carbon.model.Evidence;
import com.carbon.model.UserBadge;
import com.carbon.model.User;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserBadgeRepository;
import com.carbon.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class ProfileController {
    
    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @GetMapping("/profile")
    @Transactional
    public String profile(Authentication auth, Model model){
        User user = userRepository.findByUsername(auth.getName());
        List<Evidence> evidenceList = evidenceRepository.findByUserIdWithChallenge(user.getId());
        String selectedBadgeName = userBadgeRepository.findByUserId(user.getId())
            .map(UserBadge::getBadgeName)
            .orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("evidence", evidenceList);
        model.addAttribute("selectedBadgeName", selectedBadgeName);
        return "profile";
    }

    @GetMapping("/profile.html")
    public String redirectToProfile() {
        return "redirect:/profile";
    }
}
