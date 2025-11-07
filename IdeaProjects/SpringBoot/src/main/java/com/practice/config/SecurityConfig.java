package com.practice.config;

import com.practice.service.CustomOidcUserService;
import com.practice.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @EnableWebSecurity  // for @PreAuthorize("USER")


// @PreAuthorize("USER")
// @GetMapping
// public ResponsEntity<User> getUserById()


// @PreAuthorize("ADMIN")
// @PostMapping
// vs
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomOidcUserService oidcUserService;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomOidcUserService oidcUserService) {
        this.userDetailsService = userDetailsService;
        this.oidcUserService = oidcUserService;

    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // === 1. enable CORS + CSRF Token（Cookie） ===
                //.cors(Customizer.withDefaults())
                //.csrf(csrf -> csrf
                //        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/v2/api/**")) // no frond end
                // === 2. role based policy ===
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        // OAuth API
                        .requestMatchers("/", "/public/**", "/actuator/health", "/oauth2/**", "/login/**").permitAll()
                        // READ：USER & ADMIN are allowed
                        .requestMatchers(HttpMethod.GET, "/v2/api/user/**").hasAnyRole("USER","ADMIN")
                        // WRITE: ADMIN Only
                        .requestMatchers(HttpMethod.POST, "/v2/api/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/v2/api/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/v2/api/user/**").hasRole("ADMIN")
                        // for any other request: require login
                        .anyRequest().authenticated()
                )
                // === 3. support Basic Auth + OAuth2 Login ===
                .httpBasic(Customizer.withDefaults())
                .oauth2Login(oauth -> oauth.userInfoEndpoint(u -> u.oidcUserService(oidcUserService)))
                // === 4. allow logout ===
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
