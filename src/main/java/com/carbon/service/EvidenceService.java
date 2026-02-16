package com.carbon.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.model.User;
import com.carbon.model.Challenge;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.UserRepository;
import com.carbon.repository.ChallengeRepository;

@Service
public class EvidenceService {
    private final EvidenceRepository evidenceRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    public EvidenceService(EvidenceRepository evidenceRepository, UserRepository userRepository, ChallengeRepository challengeRepository) {
        this.evidenceRepository = evidenceRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
    }

    public Evidence submitEvidence(String username, MultipartFile photo, String taskTitle, Long challengeId) throws IOException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is required.");
        }
        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image uploads are allowed.");
        }

        Evidence evidence = new Evidence();
        evidence.setUser(user);
        evidence.setOriginalFilename(photo.getOriginalFilename());
        evidence.setContentType(contentType);
        evidence.setSizeBytes(photo.getSize());
        evidence.setTaskTitle(taskTitle);
        evidence.setPhoto(photo.getBytes());
        evidence.setStatus(EvidenceStatus.PENDING);
        evidence.setSubmittedAt(LocalDateTime.now());

        // Link evidence to challenge if challengeId is provided
        if (challengeId != null) {
            Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + challengeId));
            evidence.setChallenge(challenge);
        }

        return evidenceRepository.save(evidence);
    }

    public List<Evidence> getEvidenceByStatus(EvidenceStatus status) {
        return evidenceRepository.findByStatus(status);
    }

    public Evidence updateEvidenceStatus(Long evidenceId, EvidenceStatus status) {
        Evidence evidence = evidenceRepository.findById(evidenceId)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));
        evidence.setStatus(status);
        return evidenceRepository.save(evidence);
    }

    public Evidence getEvidence(Long evidenceId) {
        return evidenceRepository.findById(evidenceId)
            .orElseThrow(() -> new IllegalArgumentException("Evidence not found: " + evidenceId));
    }
}
