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

        // Znajdź w bazie obiekt User o danym username
        User user = userService.getUserByUsername(username);

        /*
         * 1) Tworzymy listę ról/authority. Na start można na sztywno ustawić np. "ROLE_USER"
         *    lub możesz mieć pole w encji User do przechowywania roli.
         * 2) Zwracamy obiekt klasy, która implementuje UserDetails.
         */
        return new LoggedUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}