package com.carbon.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");

    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id; // Unique identifier
    
    private String username; // Unique login name
    private String password; // BCrypt-hashed password
    private int points = 0; // Total points earned from challenges and travel
    private String role; // User role: "USER" or "MODERATOR"
    private String campus = "streatham"; // User's campus location (default: Streatham)
    
    @Column(name = "\"year\"") // "year" is a reserved SQL keyword, so we escape it
    private String year = "First_Year"; // Academic year of the user

    @ManyToMany(mappedBy = "members")
    private Set<Group> groups = new HashSet<>();

    public static String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }

    public static boolean isValidUsername(String username) {
        String normalizedUsername = normalizeUsername(username);
        return normalizedUsername.length() >= MIN_USERNAME_LENGTH
            && USERNAME_PATTERN.matcher(normalizedUsername).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static String getCredentialErrorCode(String username, String password) {
        String normalizedUsername = normalizeUsername(username);

        if (normalizedUsername.isEmpty() || password == null || password.isEmpty()) {
            return "empty";
        }

        if (!isValidUsername(normalizedUsername)) {
            return "invalid_username";
        }

        if (!isValidPassword(password)) {
            return "invalid_password";
        }

        return null;
    }

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