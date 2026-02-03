package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.carbon.model.Challenge;
import com.carbon.repository.ChallengeRepository;

import java.util.logging.Logger;

public class ChallengePageController {
    
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private ChallengeRepository challengRepository;
}
