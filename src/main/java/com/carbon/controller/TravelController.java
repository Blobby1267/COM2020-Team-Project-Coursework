package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.ChallengeService;
import com.carbon.service.TravelService;

import org.springframework.ui.Model;
import java.util.logging.Logger;
import org.springframework.security.core.Authentication;

@Controller
public class TravelController {
    
    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @PostMapping("/api/travel/submit")
    public String completeChallenge(
        @RequestParam String travelType,
        @RequestParam int distance,
        Authentication authentication
    ) {
        travelService.registerTravel(authentication.getName(), travelType, distance);
        return "redirect:/travel?success";
    }
}
