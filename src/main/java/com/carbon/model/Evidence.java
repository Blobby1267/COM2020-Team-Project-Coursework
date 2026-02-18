package com.carbon.model;

import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity class representing photo evidence submitted by users for challenges.
 * Mapped to the "evidence" table in the database.
 * Used by moderators for reviewing submissions and awarding points.
 */
@Entity
@Table(name = "evidence")
public class Evidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many evidence submissions belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Evidence may optionally be linked to a specific challenge
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column(nullable = false)
    private String originalFilename; // Original filename of uploaded photo

    @Column(nullable = false)
    private String contentType; // photo type (e.g., "image/jpeg", "image/png")

    private long sizeBytes; // File size in bytes

    private String taskTitle; // title for task

    // Binary photo data
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] photo;

    // Current moderation status (PENDING, ACCEPTED, or REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvidenceStatus status = EvidenceStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime submittedAt; // Timestamp of submission

    //Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public EvidenceStatus getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    //Setters
    
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setStatus(EvidenceStatus status) {
        this.status = status;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
