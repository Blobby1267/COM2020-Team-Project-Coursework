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

@RestController
@RequestMapping("/api")
public class EvidenceController {
    private final EvidenceService evidenceService;

    public EvidenceController(EvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @PostMapping(path = "/evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceSummary> submitEvidence(
        @RequestParam("photo") MultipartFile photo,
        @RequestParam(value = "taskTitle", required = false) String taskTitle,
        Authentication authentication
    ) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Evidence evidence = evidenceService.submitEvidence(authentication.getName(), photo, taskTitle);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSummary(evidence));
    }

    @GetMapping("/evidence/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable("id") Long id) {
        Evidence evidence = evidenceService.getEvidence(id);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (evidence.getContentType() != null) {
            mediaType = MediaType.parseMediaType(evidence.getContentType());
        }
        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + evidence.getOriginalFilename() + "\"")
            .body(evidence.getPhoto());
    }

    @GetMapping("/moderator/evidence")
    public List<EvidenceSummary> listEvidence(
        @RequestParam(value = "status", defaultValue = "PENDING") EvidenceStatus status
    ) {
        return evidenceService.getEvidenceByStatus(status).stream()
            .map(this::toSummary)
            .toList();
    }

    @PostMapping("/moderator/evidence/{id}/status")
    public ResponseEntity<EvidenceSummary> updateStatus(
        @PathVariable("id") Long id,
        @RequestParam("status") EvidenceStatus status
    ) {
        Evidence evidence = evidenceService.updateEvidenceStatus(id, status);
        return ResponseEntity.ok(toSummary(evidence));
    }

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
