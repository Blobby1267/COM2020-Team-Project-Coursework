package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carbon.model.Challenge;
import com.carbon.repository.ChallengeRepository;
import com.carbon.service.ChallengeService;
import java.util.List;
import org.springframework.ui.Model;
import java.util.logging.Logger;

@Controller
public class ChallengePageController {
    
    private final ChallengeService challengeService;

    public ChallengePageController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private ChallengeRepository challengeRepository;

    @GetMapping("/tasks")
    public String challenge(Model model) {
        model.addAttribute("dailyChallenges", challengeService.getChallengesByFrequency("Daily"));
        model.addAttribute("weeklyChallenges", challengeService.getChallengesByFrequency("Weekly"));
        model.addAttribute("monthlyChallenges", challengeService.getChallengesByFrequency("Monthly"));
        return "tasks";
    }

    @GetMapping("/tasks.html")
    public String redirectToTasks() {
        return "redirect:/tasks";
    }
}
