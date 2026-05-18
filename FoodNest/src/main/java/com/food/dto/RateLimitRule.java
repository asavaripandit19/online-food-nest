package com.food.dto;

public record RateLimitRule(int limit, int windowSeconds) {
	
}