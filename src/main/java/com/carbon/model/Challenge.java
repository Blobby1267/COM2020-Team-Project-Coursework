package com.carbon.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

/**
 * Entity class representing a sustainability challenge that users can complete.
 * Mapped to the "challenges" table in the database.
 * Used throughout the application for displaying available challenges and awarding points.
 */
@Entity // This class maps to a database table
@Table(name = "challenges")
public class Challenge {
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;
    
    private String title; // Display name of the challenge (e.g., "Walk to Campus")
    private String description; // Detailed description of what user must do
    private int points; // Points awarded when challenge is completed
    private String frequency; // How often challenge resets: "Daily", "Weekly", or "Monthly"
    private Date startDate; // When challenge becomes available
    private Date endDate; // When challenge expires (null = no expiration)
    private String scope; // Scope of challenge
    private boolean requiresEvidence; // Whether photo evidence must be submitted
    private String taxonomy; // Category of challenge
    private Double carbonSaved; // Estimated CO2 reduction in kg

    // Getters
    
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getPoints() {
        return points;
    }
    public String getFrequency() {
        return frequency;
    }
    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public String getScope() {
        return scope;
    }
    public boolean isRequiresEvidence() {
        return requiresEvidence;
    }
    public String getTaxonomy() {
        return taxonomy;
    }
    public Double getCarbonSaved() {
        return carbonSaved;
    }

    // setters
    
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public void setRequiresEvidence(boolean requiresEvidence) {
        this.requiresEvidence = requiresEvidence;
    }
    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }
    public void setCarbonSaved(Double carbonSaved) {
        this.carbonSaved = carbonSaved;
    }
}
