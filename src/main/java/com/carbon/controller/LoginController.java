package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import
 com.carbon.model.User;
import com.carbon.repository.UserRepository;
import java.util.logging.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }


    /* 
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            LOGGER.info("User has successfully signed in.");
            return "redirect:/tasks.html"; // Success!
        }
        LOGGER.info("User password is incorrect.");
        return "redirect:/login.html?error=true"; // Try again
    }
    */ 
    @PostMapping("/register")
    public String handleRegister(@RequestParam String username, @RequestParam String password, @RequestParam String campus) {
        if (userRepository.findByUsername(username) == null) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole("USER");
            newUser.setPoints(0);
            newUser.setCampus(campus);
            registerUser(newUser);
            LOGGER.info("User has been created.");
            return "redirect:/tasks.html?registered=true";
        }
        LOGGER.info("User already exists.");
        return "redirect:/login.html?error=exists";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/login.html")
    public String redirectToLogin() {
        return "redirect:/login";
    }


}
