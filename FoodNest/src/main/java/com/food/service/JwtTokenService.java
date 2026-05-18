package com.food.service;

import java.util.UUID;

public interface JwtTokenService {

    String generateToken(UUID userId, String email, String role);

    boolean validateToken(String token);

    UUID extractUserId(String token);

    String extractEmail(String token);

    String extractRole(String token);
}