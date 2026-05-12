package com.food.service;

public interface JwtTokenService {

	public String generateToken(Long userId, String role);
}
