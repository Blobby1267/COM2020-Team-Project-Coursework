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

@Service
public class TravelService {

    private final UserRepository userRepository;
    private static final int POINTS_PER_KM = 5;

    public TravelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

    public Map<String, Object> registerTravel(String username, String travelType, double distance) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        int pointsEarned = (int)(POINTS_PER_KM * distance);
        user.setPoints(user.getPoints() + pointsEarned);
        userRepository.save(user);
        
        Map<String, Object> result = new HashMap<>();
        result.put("travelType", travelType);
        result.put("distance", distance);
        result.put("pointsPerKm", POINTS_PER_KM);
        result.put("pointsEarned", pointsEarned);
        result.put("totalPoints", user.getPoints());
        
        return result;
    }
}