package com.food.config;

import com.food.filter.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // =========================
    // JWT FILTER
    // =========================

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // =========================
    // PASSWORD ENCODER
    // =========================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =========================
    // AUTHENTICATION MANAGER
    // =========================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    // =========================
    // SECURITY FILTER CHAIN
    // =========================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =========================
                // DISABLE CSRF
                // =========================

                .csrf(csrf -> csrf.disable())

                // =========================
                // STATELESS SESSION
                // =========================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =========================
                // AUTHORIZATION RULES
                // =========================

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC APIs
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ADMIN APIs
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // VENDOR APIs
                        .requestMatchers("/vendor/**")
                        .hasAnyRole(
                                "VENDOR",
                                "ADMIN"
                        )

                        // CUSTOMER APIs
                        .requestMatchers("/customer/**")
                        .hasRole("CUSTOMER")

                        // OTHER APIs
                        .anyRequest()
                        .authenticated()
                )

                // =========================
                // JWT FILTER
                // =========================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}