package com.food.service;

import com.food.enums.OtpPurpose;

public interface OtpService {

//	 public String sendOtp(String mobile, OtpPurpose purpose);
	 public String sendEmailOtp(String email, OtpPurpose purpose);
	 public String generateAndSaveOtp(String mobile, OtpPurpose purpose);
	 public void validateEmail(String email);
}
