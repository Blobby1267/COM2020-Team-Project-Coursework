package com.carbon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.carbon.model.Evidence;
import com.carbon.model.User;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class ProfileController {
    
    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    @Transactional
    public String profile(Authentication auth, Model model){
        User user = userRepository.findByUsername(auth.getName());
        List<Evidence> evidenceList = evidenceRepository.findByUserIdWithChallenge(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("evidence", evidenceList);
        return "profile";
    }

    @GetMapping("/profile.html")
    public String redirectToProfile() {
        return "redirect:/profile";
    }
}
