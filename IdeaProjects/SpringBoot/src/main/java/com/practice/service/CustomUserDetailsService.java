package com.practice.service;

import com.practice.dao.sql.JpaUserRepository;
import com.practice.model.User;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;


/**
 * implement UserDetailsService + @Service => detected by spring security
 * injected into SecurityConfig
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUserRepository repo;

    public CustomUserDetailsService(JpaUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // System.out.println(">>> loading user: " + username);
        User u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        System.out.println(">>> loading user: " + u.getUsername() + "(" + u.getPassword() + ")");
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(u.getRole())
                .disabled(!u.isEnabled())
                .build();
    }
}
