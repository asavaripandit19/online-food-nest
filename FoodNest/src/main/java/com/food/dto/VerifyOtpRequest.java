package com.food.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {

	private String mobile;
	private String otp;
	private String email;
}
