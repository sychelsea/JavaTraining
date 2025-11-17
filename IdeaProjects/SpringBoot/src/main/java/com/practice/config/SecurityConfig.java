package com.practice.config;

import com.practice.service.CustomOidcUserService;
import com.practice.service.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.http.client.reactive.AbstractClientHttpConnectorProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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


    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomOidcUserService oidcUserService, CorsConfigurationSource corsConfigurationSource) {
        this.userDetailsService = userDetailsService;
        this.oidcUserService = oidcUserService;
    }


    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/v2/api/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/v2/api/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v2/api/user").permitAll() // allow anonymous registration

                        // rules for user APIs
                        .requestMatchers(HttpMethod.GET, "/v2/api/user/**")
                        .hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/v2/api/user/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/v2/api/user/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())   // Basic Auth
                .oauth2Login(oauth -> oauth.disable()); // disable OAuth2

        return http.build();
    }


    @Bean PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // "Authorization","Content-Type"
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
