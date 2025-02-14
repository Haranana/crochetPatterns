package com.example.crochetPatterns.services;

import com.example.crochetPatterns.others.LoggedUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public boolean isLogged(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof LoggedUserDetails;
    }

    public LoggedUserDetails getLoggedUserDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (LoggedUserDetails) auth.getPrincipal();
    }
}
