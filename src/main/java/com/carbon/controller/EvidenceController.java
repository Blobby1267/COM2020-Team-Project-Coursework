package com.carbon.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carbon.model.Evidence;
import com.carbon.model.EvidenceStatus;
import com.carbon.service.EvidenceService;

/**
 * Rest API controller for managing evidence submissions.
 * Handles photo uploads, retrieval, and moderation (accept/reject).
 */
@RestController
@RequestMapping("/api")
public class EvidenceController {
    // Calls evidence service
    private final EvidenceService evidenceService;
    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    /**
     * API endpoint for users to submit photo evidence for challenges.
     * Accepts multipart/form-data with photo file and optional metadata.
     * @param photo - The uploaded image file
     * @param taskTitle - title for the task
     * @param challengeId - ID to link evidence to a specific challenge
     * @param authentication - Security authentication for the current user
     * @return ResponseEntity with EvidenceSummary (201 CREATED) or 401 UNAUTHORIZED
     * @throws IOException if file reading fails
     */
    @PostMapping(path = "/evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceSummary> submitEvidence(
        @RequestParam("photo") MultipartFile photo,
        @RequestParam(value = "taskTitle", required = false) String taskTitle,
        @RequestParam(value = "challengeId", required = false) Long challengeId,
        Authentication authentication
    ) throws IOException {
        // Check user is logged in
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Delegate to service layer
        Evidence evidence = evidenceService.submitEvidence(authentication.getName(), photo, taskTitle, challengeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSummary(evidence));
    }

    /**
     * Retrieves the actual image file for a given evidence submission.
     * Returns image data with appropriate content type header.
     * @param id - The ID of the evidence whose photo to retrieve
     * @return ResponseEntity with byte array containing the image data
     */
    @GetMapping("/evidence/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        Evidence evidence = evidenceService.getEvidence(id);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        // Check if content type is available and valid before parsing
        if (evidence.getContentType() != null) {
            mediaType = MediaType.parseMediaType(evidence.getContentType());
        }
        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + evidence.getOriginalFilename() + "\"")
            .body(evidence.getPhoto());
    }

    /**
     * Moderator endpoint to list evidence submissions filtered by status.
     * @param status - Filter by evidence status
     * @return List of objects matching the status
     * 
     * Used by moderators to review pending submissions or check previously reviewed evidence.
     */
    @GetMapping("/moderator/evidence")
    public List<EvidenceSummary> listEvidence(
        @RequestParam(value = "status", defaultValue = "PENDING") EvidenceStatus status
    ) {
        // Fetch evidence by status and convert to lightweight summaries (excludes photo bytes)
        return evidenceService.getEvidenceByStatus(status).stream()
            .map(this::toSummary)
            .toList();
    }

    /**
     * Moderator endpoint to update the status of a piece of evidence.
     * @param id - The ID of the evidence to update
     * @param status - New status
     * @return ResponseEntity
     */
    @PostMapping("/moderator/evidence/{id}/status")
    public ResponseEntity<EvidenceSummary> updateStatus(
        @PathVariable("id") Long id,
        @RequestParam("status") EvidenceStatus status
    ) {
        // sends to service 
        Evidence evidence = evidenceService.updateEvidenceStatus(id, status);
        return ResponseEntity.ok(toSummary(evidence));
    }

    /**
     * Method to convert Evidence entity to EvidenceSummary DTO.
     * @param id - The ID of the evidence to update
     * @param evidence - Full evidence entity from database
     * @return EvidenceSummary
     */
    private EvidenceSummary toSummary(Evidence evidence) {
        String username = evidence.getUser() != null ? evidence.getUser().getUsername() : null;
        return new EvidenceSummary(
            evidence.getId(),
            username,
            evidence.getTaskTitle(),
            evidence.getStatus(),
            evidence.getSubmittedAt(),
            evidence.getOriginalFilename()
        );
    }

    /**
     * Data Object for evidence information.
     * Contains key data without binary photo data.
     * Used for listing evidence and returning submission confirmations.
     */
    public static class EvidenceSummary {
        private final Long id;
        private final String username;
        private final String taskTitle;
        private final EvidenceStatus status;
        private final LocalDateTime submittedAt;
        private final String originalFilename;

        public EvidenceSummary(
            Long id,
            String username,
            String taskTitle,
            EvidenceStatus status,
            LocalDateTime submittedAt,
            String originalFilename
        ) {
            this.id = id;
            this.username = username;
            this.taskTitle = taskTitle;
            this.status = status;
            this.submittedAt = submittedAt;
            this.originalFilename = originalFilename;
        }

        // getters
        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getTaskTitle() {
            return taskTitle;
        }

        public EvidenceStatus getStatus() {
            return status;
        }

        public LocalDateTime getSubmittedAt() {
            return submittedAt;
        }

        public String getOriginalFilename() {
            return originalFilename;
        }
    }
}
