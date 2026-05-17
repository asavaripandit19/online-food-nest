package com.food.service;

import com.food.dto.LoginRequest;
import com.food.dto.UsernamePasswordRequest;
import com.food.enums.Role;

public interface AuthService {

	String verifyMobileOtp(String mobile, String otp);

	String signupMobileSendOtp(String mobile, Role role);

	String signupEmailSendOtp(String email, Role role);

	String verifyEmailOtp(String email, String otp);

	String login(LoginRequest request);

	String loginVerifyMobileOtp(String mobile, String otp);

	String verifyEmailLoginOtp(String email, String otp);

	String sendLoginMobileOtp(String mobile, Role role);

	String sendLoginEmailOtp(String email, Role role);

	public String signupWithUsernamePassword(UsernamePasswordRequest request, Role role);

	void validateMobile(String mobile);

	void validateEmail(String email);
}