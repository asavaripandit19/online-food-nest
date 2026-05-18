package com.food.ServiceImpl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.food.service.JwtTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // =========================
    // GENERATE TOKEN
    // =========================
    @Override
    public String generateToken(UUID userId, String email, String role) {

        return Jwts.builder()
                .setSubject(userId.toString())   // ✅ BEST PLACE for userId
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // =========================
    // CLAIMS
    // =========================
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // =========================
    // VALIDATE TOKEN
    // =========================
    @Override
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // EXTRACT USER ID (UUID)
    // =========================
    @Override
    public UUID extractUserId(String token) {
        String userId = getClaims(token).getSubject();
        return UUID.fromString(userId);
    }

    // =========================
    // EXTRACT EMAIL
    // =========================
    @Override
    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    // =========================
    // EXTRACT ROLE
    // =========================
    @Override
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }
}