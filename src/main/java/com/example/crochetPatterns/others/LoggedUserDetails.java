package com.example.crochetPatterns.others;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LoggedUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final boolean enabled;

    public LoggedUserDetails(Long id, String username, String password, boolean enabled) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
