package com.carbon.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import com.carbon.repository.UserRepository;
import com.carbon.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username);

       return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }
}
