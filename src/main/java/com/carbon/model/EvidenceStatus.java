package com.carbon.model;

/**
 * Enumeration representing the moderation status of evidence submissions.
 * - PENDING: Awaiting moderator review (default status on submission)
 * - ACCEPTED: Approved by moderator, points awarded to user
 * - REJECTED: Denied by moderator, no points awarded
 * 
 * Used in Evidence entity and throughout the moderation workflow.
 */
public enum EvidenceStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
