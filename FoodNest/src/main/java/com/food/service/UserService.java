package com.food.service;

public interface UserService {
	public String sendOtp(String mobile);
	public String verifyOtp(String mobile, String otp);
	public String login(String email, String password);
}
