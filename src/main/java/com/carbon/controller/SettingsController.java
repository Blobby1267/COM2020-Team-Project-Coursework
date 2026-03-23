package com.carbon.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.carbon.model.User;
import com.carbon.model.Group;
import com.carbon.repository.BadgeRepository;
import com.carbon.repository.EvidenceRepository;
import com.carbon.repository.GroupRepository;
import com.carbon.repository.LeaderboardRepository;
import com.carbon.repository.UserRepository;
import com.carbon.repository.UserBadgeRepository;
import com.carbon.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class SettingsController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired 
    private UserRepository userRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private GroupRepository groupRepository;

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
    @Transactional
    public String deleteAccount(@RequestParam String username, @RequestParam String password, Authentication authentication, Model model, HttpServletRequest request){
        User user = userRepository.findByUsername(authentication.getName());

        if(!user.getUsername().equals(username) || !passwordEncoder.matches(password, user.getPassword())){
            model.addAttribute("deleteError", "Incorrect username or password");
            return "settings";
        }

        Long userId = user.getId();

        List<Group> ownedGroups = groupRepository.findByOwner_Id(userId);
        Set<Integer> ownedGroupIds = new HashSet<>();
        for (Group ownedGroup : ownedGroups) {
            ownedGroupIds.add(ownedGroup.getId());
        }

        List<Group> memberGroups = groupRepository.findByMembers_Id(userId);
        for (Group group : memberGroups) {
            if (ownedGroupIds.contains(group.getId())) {
                continue;
            }
            group.getMembers().remove(user);
            groupRepository.save(group);
        }

        if (!ownedGroups.isEmpty()) {
            groupRepository.deleteAll(ownedGroups);
        }

        evidenceRepository.deleteByUser_Id(userId);
        badgeRepository.deleteByUserId(userId);
        userBadgeRepository.deleteByUserId(userId);
        leaderboardRepository.deleteByUserId(userId);

        userRepository.delete(user);
        request.getSession().invalidate();
        return "redirect:/login?accountdeleted=true";
    }
}
