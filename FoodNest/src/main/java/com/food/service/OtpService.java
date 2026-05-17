package com.food.service;

import com.food.enums.OtpPurpose;
import com.food.enums.Role;

public interface OtpService {

//	 public String sendOtp(String mobile, OtpPurpose purpose);
	public String sendEmailOtp(String email, OtpPurpose purpose);

	public void validateEmail(String email);

	String generateAndSaveOtp(String mobile, OtpPurpose purpose);

	String verifyEmailOtp(String email, String otp, OtpPurpose purpose, Role role);

	public String verifyOtp(String mobile, String otp, OtpPurpose purpose, Role role);

}
