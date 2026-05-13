package com.food.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // =========================
    // PASSWORD ENCODER
    // =========================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =========================
    // SECURITY FILTER CHAIN
    // =========================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // Disable CSRF for JWT APIs
                .csrf(csrf -> csrf.disable())

                // Stateless Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC APIs
                        // =========================

                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // =========================
                        // VENDOR APIs
                        // =========================

                        .requestMatchers("/vendor/**")
                        .hasAnyRole("VENDOR", "ADMIN")

                        // =========================
                        // ADMIN APIs
                        // =========================

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // =========================
                        // OTHER APIs
                        // =========================

                        .anyRequest()
                        .authenticated()
                );

        return http.build();
    }
}