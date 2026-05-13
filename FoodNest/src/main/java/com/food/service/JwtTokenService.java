package com.food.service;

public interface JwtTokenService {

	public String generateToken(Long userId, String role);
	boolean validateToken(String token);

	String extractRole(String token);
}
