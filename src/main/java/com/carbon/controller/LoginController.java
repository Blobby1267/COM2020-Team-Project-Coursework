package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.carbon.model.User;
import com.carbon.repository.UserRepository;
import java.util.logging.Logger;

@Controller
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            LOGGER.info("User has successfully signed in.");
            return "redirect:/dashboard.html"; // Success!
        }
        LOGGER.info("User password is incorrect.");
        return "redirect:/webpage.html?error=true"; // Try again
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username, @RequestParam String password) {
        if (userRepository.findByUsername(username) == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setPoints(0); // Default points
            userRepository.save(newUser);
            LOGGER.info("User has been created.");
            return "redirect:/webpage.html?registered=true";
        }
        LOGGER.info("User already exists.");
        return "redirect:/webpage.html?error=exists";
    }
}
