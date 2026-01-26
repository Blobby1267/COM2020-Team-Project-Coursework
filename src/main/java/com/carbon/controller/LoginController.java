package com.carbon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.carbon.model.User;
import com.carbon.repository.UserRepository;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/dashboard.html"; // Success!
        }
        return "redirect:/webpage.html?error=true"; // Try again
    }
}
