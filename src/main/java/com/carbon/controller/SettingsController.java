package com.carbon.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.carbon.model.User;
import com.carbon.repository.UserRepository;
import com.carbon.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class SettingsController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/update_username")
    public String updateUsername(@RequestParam String oldUsername, @RequestParam String  newUsername, Authentication authentication){
        String normalizedOldUsername = User.normalizeUsername(oldUsername);
        String normalizedNewUsername = User.normalizeUsername(newUsername);

        if(normalizedOldUsername.equals(authentication.getName())
            && User.isValidUsername(normalizedNewUsername)
            && (userRepository.findByUsername(normalizedNewUsername) == null || normalizedNewUsername.equals(authentication.getName()))){
            User user = userRepository.findByUsername(normalizedOldUsername);
            user.setUsername(normalizedNewUsername);
            userRepository.save(user);
            UserDetails updatedUser = new org.springframework.security.core.userdetails.User(normalizedNewUsername, user.getPassword(), authentication.getAuthorities());
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(updatedUser, authentication.getCredentials(), authentication.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            return "redirect:/settings?updatedname=true";
        }
        return "redirect:/settings?updatedname=false";
    }
    
    
    @PostMapping("/update_password")
    public String updatePassword(@RequestParam String oldPassword, @RequestParam String  newPassword, Authentication authentication){
        if (!User.isValidPassword(newPassword)) {
            return "redirect:/settings?updatedpassword=false";
        }

        userDetailsService.updatePassword(authentication.getName(), oldPassword, newPassword);
        return "redirect:/settings?updatedpassword=true";
    } 

    
    @PostMapping("/update_year")
    public String updateYear(@RequestParam String year, Authentication authentication){
        List<String> years = Arrays.asList("First_Year", "Second_Year", "Third_Year", "Fourth_Year", "PostGraduate", "Staff");
        if(years.contains(year)){
            User user = userRepository.findByUsername(authentication.getName());
            user.setYear(year);
            userRepository.save(user);
            return "redirect:/settings?updatedyear=true";
        }
        return "redirect:/settings?updatedyear=false";
    } 

    
    @PostMapping("/update_campus")
    public String updateCampus(@RequestParam String campus, Authentication authentication){
        List<String> campusList = Arrays.asList("Streatham", "St Lukes", "Penryn", "Truro");
        if(campusList.contains(campus)){
            User user = userRepository.findByUsername(authentication.getName());
            user.setCampus(campus);
            userRepository.save(user);
            return "redirect:/settings?updatedcampus=true";
        }
        return "redirect:/settings?updatedcampus=false";
    } 

    @PostMapping("/delete_account")
    public String deleteAccount(@RequestParam String username, @RequestParam String password, Authentication authentication, Model model, HttpServletRequest request){
        User user = userRepository.findByUsername(authentication.getName());

        if(!user.getUsername().equals(username) || !passwordEncoder.matches(password, user.getPassword())){
            model.addAttribute("deleteError", "Incorrect username or password");
            return "settings";
        }
        request.getSession().invalidate();
        userRepository.delete(user);
        return "redirect:/goodbye";
    }
}
