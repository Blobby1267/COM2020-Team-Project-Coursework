package com.carbon.service;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.carbon.repository.ChallengeRepository;
import com.carbon.repository.UserRepository;
import com.carbon.model.Challenge;
import com.carbon.model.User;
import java.util.List;

@Service
public class TravelService {

    private final UserRepository userRepository;

    public TravelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

    public void registerTravel(String username, String travelType, int distance) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        user.setPoints(user.getPoints() + 5*distance);
        userRepository.save(user);
    }
}