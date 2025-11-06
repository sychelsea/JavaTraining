package com.practice.config;

import com.practice.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// @EnableWebSecurity  // for @PreAuthorize()
public class SecurityConfig {

    private final CustomUserDetailsService uds;

    public SecurityConfig(CustomUserDetailsService uds) {
        this.uds = uds;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // READ：USER & ADMIN are allowed
                        .requestMatchers(HttpMethod.GET, "/v2/api/user/**").hasAnyRole("USER","ADMIN")
                        // WRITE: ADMIN Only
                        .requestMatchers(HttpMethod.POST, "/v2/api/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/v2/api/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/v2/api/user/**").hasRole("ADMIN")
                        // for any other request: require login
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}