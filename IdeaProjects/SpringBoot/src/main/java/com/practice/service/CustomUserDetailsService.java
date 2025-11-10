package com.practice.service;

import com.practice.dao.sql.JpaUserRepository;
import com.practice.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * implement UserDetailsService + @Service => detected by spring security
 * injected into SecurityConfig
 */

//Client Request
//  ↓
//SecurityContextPersistenceFilter
//  ↓
//LogoutFilter /logout
//  ↓
//UsernamePasswordAuthenticationFilter
//  ↓
//BasicAuthenticationFilter
//  ↓
//BearerTokenAuthenticationFilter
//  ↓
//ExceptionTranslationFilter
//  ↓
//FilterSecurityInterceptor
//  ↓
//Your Controller
//
//
//CustomUserDetailsService implements UserDetailsService
//
//JwtAuthenticationFilter extends OncePerRequestFilter
//
//
//class jwtTokenGenerator() {
//
//
//
//}
//
//
//
//class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//    }
//}

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
        //System.out.println(">>> loading user: " + u.getUsername() + "(" + u.getPassword() + ")");
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .authorities(u.getRole())
                .disabled(!u.isEnabled())
                .build();
    }
}
