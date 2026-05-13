package com.food.ServiceImpl;



import java.util.Date;

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
    
    
	@Override
	public String generateToken(Long userId, String role) {
		return Jwts.builder()
                .claim("userId", userId) 
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


	 // =========================
    // VALIDATE TOKEN
    // =========================

    @Override
    public boolean validateToken(String token) {

        try {

            Jwts.parser()

                    .setSigningKey(secret)

                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    // =========================
    // EXTRACT ROLE
    // =========================

    @Override
    public String extractRole(String token) {

        Claims claims = Jwts.parser()

                .setSigningKey(secret)

                .parseClaimsJws(token)

                .getBody();

        return claims.get("role", String.class);
    }
		
	

}
