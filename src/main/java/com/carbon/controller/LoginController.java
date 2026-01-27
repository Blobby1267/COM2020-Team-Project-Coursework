package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.carbon.model.User;
import com.carbon.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/main_screen.html"; // Success!
        }
        return "redirect:/login.html?error=true"; // Try again
    }

    @PostMapping("/register")
    @Transactional
    public String handleRegister(@RequestParam String username, @RequestParam String password) {
        if (userRepository.findByUsername(username) == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setPoints(0); // Default points
            userRepository.save(newUser);
            return "redirect:/main_screen.html?registered=true";
        }
        return "redirect:/login.html?error=exists";
    }
}
