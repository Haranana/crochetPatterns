package com.example.crochetPatterns.services;

import com.example.crochetPatterns.entities.User;
import com.example.crochetPatterns.others.LoggedUserDetails;
import com.example.crochetPatterns.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.getUserByUsername(username);

        return new LoggedUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isEnabled()
        );
    }
}