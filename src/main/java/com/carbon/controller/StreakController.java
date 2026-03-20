package com.carbon.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.carbon.model.DataForAnalytics;
import com.carbon.model.User;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.service.StreakService;

@ControllerAdvice
public class StreakController {

    private final UserRepository userRepository;
    private final EvidenceRepository evidenceRepository;
    private final StreakService streakService;

    public StreakController(
        UserRepository userRepository,
        EvidenceRepository evidenceRepository,
        StreakService streakService
    ) {
        this.userRepository = userRepository;
        this.evidenceRepository = evidenceRepository;
        this.streakService = streakService;
    }

    @ModelAttribute("streak")
    public int streak(Authentication auth) {
        if (auth == null) {
            return 0;
        }

        User user = userRepository.findByUsername(auth.getName());
        if (user == null) {
            return 0;
        }

        List<DataForAnalytics> acceptedEvidence = evidenceRepository.findAcceptedEvidenceByUserId(user.getId());
        return streakService.calculateStreak(acceptedEvidence);
    }
}
