package com.food.service;

import com.food.dto.LoginRequest;
import com.food.dto.SignupRequest;
import com.food.enums.Role;

public interface AuthService {

	public String verifySignupOtp(String mobile, String otp);



	public String login(LoginRequest request);

	public String loginVerifyOtp(String mobile, String otp);

	public String verifyEmailSignupOtp(String email, String otp);

	public String forgotPasswordSendOtp(String email);

	public String verifyForgotPasswordOtp(String email, String otp);

	public String resetPassword(String email, String otp, String newPassword);

	public void validateMobile(String mobile);

	public void validateEmail(String email);

	public String verifyEmailLoginOtp(String email, String otp);

	public String signup(SignupRequest request, Role role);
}
