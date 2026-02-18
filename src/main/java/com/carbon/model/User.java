package com.carbon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Entity class representing a user account in the system.
 * Mapped to the "users" table in the database.
 * Stores authentication credentials, points, role, and profile information.
 * Central entity linked to Evidence, Challenges, and Leaderboard functionality.
 */
@Entity // Tells JPA this class maps to a database table
@Table(name = "users")
public class User {
    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id; // Unique identifier
    
    private String username; // Unique login name
    private String password; // BCrypt-hashed password (never stored in plain text)
    private int points = 0; // Total points earned from challenges and travel
    private String role; // User role: "USER", "MODERATOR", or "ADMIN"
    private String campus = "streatham"; // User's campus location (default: Streatham)
    
    @Column(name = "\"year\"") // "year" is a reserved SQL keyword, so we escape it
    private String year = "First_Year"; // Academic year of the user


    // === Getter Methods ===
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getPoints() {
        return points;
    }
    public String getRole() {
        return role;
    }
    public String getCampus() {
        return campus;
    }
    public String getYear() {
        return year;
    }

    // === Setter Methods ===
    
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setCampus(String campus) {
        this.campus = campus;
    }
    public void setYear(String year) {
        this.year = year;
    }
}