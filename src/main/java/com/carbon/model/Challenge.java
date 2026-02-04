package com.carbon.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity // Tells Java this maps to a database table
@Table(name = "challenges")
public class Challenge {
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;
    private String title;
    private String description;
    private int points;
    private String frequency;
    private Date startDate;
    private Date endDate;
    private String scope;

    //getters
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

    //setters
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
}
