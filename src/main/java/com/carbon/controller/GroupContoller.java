package com.carbon.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.carbon.repository.GroupRepository;
import com.carbon.repository.UserRepository;
import com.carbon.model.Group;
import com.carbon.model.User;

@Controller
public class GroupContoller {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("/groups")
    public String groups(Authentication auth, Model model){
        User user = userRepository.findByUsername(auth.getName());
        List<Group> groupList = groupRepository.findByMembers_Id(user.getId());
        model.addAttribute("groups", groupList);
        return "groups";
    }

    @PostMapping("/create_group")
    public String createGroup(@RequestParam String groupName, Authentication authentication){
        Group newGroup = new Group();
        String userName = authentication.getName();
        User owner = userRepository.findByUsername(userName);
        newGroup.setName(groupName);
        newGroup.setOwner(owner);
        newGroup.setInviteCode(generateInviteCode());
        groupRepository.save(newGroup);
        addUserToGroup(owner, newGroup);
        return "redirect:/groups?created=true";
    }

    @PostMapping("/join_group")
    public String joinGroup(@RequestParam String groupIdString, Authentication authentication){
        try{
            Group group = groupRepository.findByInviteCode(groupIdString);
            String userName = authentication.getName();
            User user = userRepository.findByUsername(userName);
            addUserToGroup(user, group);
        }
        catch(Exception ex){
            return "redirect:/groups?joined=false";
        }
        return "redirect:/groups?joined=true";
    }

    @PostMapping("/disband_group")
    public String disbandGroup(@RequestParam int groupId, Authentication authentication){
        try{
            Group group = groupRepository.findById(groupId);
            User user = userRepository.findByUsername(authentication.getName());
            if(group.getOwner() == user){
                groupRepository.delete(group);
                return "redirect:/groups?disband=true";
            }
            return "redirect:/groups?disband=false";
        }catch(Exception ex){
            return "redirect:/groups?disband=false";
        }
    }

    @PostMapping("/leave_group")
    public String leaveGroup(@RequestParam int groupId, Authentication authentication){
        try{
            Group group = groupRepository.findById(groupId);
            User user = userRepository.findByUsername(authentication.getName());
            if(group.getMembers().contains(user) && group.getOwner() != user){
                group.getMembers().remove(user);
                groupRepository.save(group);
                return "redirect:/groups?leave=true";
            }
            return "redirect:/groups?leave=false";
        }catch(Exception ex){
            return "redirect:/groups?leave=false";
        }
    }

    private void addUserToGroup(User user, Group group){
        group.getMembers().add(user);
        groupRepository.save(group);
    }

    private String generateInviteCode(){
        String code;
        for(int attempts = 0; attempts < 10; attempts++){
            code = UUID.randomUUID().toString().replace("-", "").substring(0,8).toUpperCase();
            if (!groupRepository.existsByInviteCode(code)){
                return code;
            }
        }
        throw new IllegalStateException("Failed to make unique code");
    }
}
