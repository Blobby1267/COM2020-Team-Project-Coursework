package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.model.Challenge;
import com.carbon.model.User;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Service layer for sustainable travel/commute tracking.
 * Calculates and awards points based on distance traveled using eco-friendly transport.
 * Points formula: 5 points per kilometer traveled.
 * Used by TravelController to process travel submissions.
 */
@Service
public class TravelService {

    // Repository for accessing and updating user data
    private final UserRepository userRepository;
    // Points awarded per kilometer traveled (constant rate)
    private static final int POINTS_PER_KM = 5;

    // Constructor injection for UserRepository
    public TravelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

    /**
     * Registers a sustainable travel journey and awards points to the user.
     * @param username - Username of the user submitting travel
     * @param travelType - Type of travel (walking, cycling, public transport, etc.)
     * @param distance - Distance traveled in kilometers
     * @return Map containing calculation details: travelType, distance, pointsPerKm, pointsEarned, totalPoints
     * @throws UsernameNotFoundException if user doesn't exist
     * 
     * Calculation:
     * - Points earned = distance (km) × 5 points/km
     * - Rounded down to nearest integer
     * 
     * Process:
     * 1. Validates user exists
     * 2. Calculates points based on distance
     * 3. Adds points to user's total
     * 4. Saves updated user to database
     * 5. Returns calculation details for display to user
     */
    public Map<String, Object> registerTravel(String username, String travelType, double distance) {
        // Validate user exists
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Calculate points: 5 points per km traveled
        int pointsEarned = (int)(POINTS_PER_KM * distance);
        user.setPoints(user.getPoints() + pointsEarned);
        userRepository.save(user); // Save updated points to database
        
        // Build result map with calculation details for display
        Map<String, Object> result = new HashMap<>();
        result.put("travelType", travelType);
        result.put("distance", distance);
        result.put("pointsPerKm", POINTS_PER_KM);
        result.put("pointsEarned", pointsEarned);
        result.put("totalPoints", user.getPoints());
        
        return result;
    }
}