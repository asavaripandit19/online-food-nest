package com.food.dto;

import lombok.Data;

@Data
public class SendOtpRequest {

	private String mobile;
	private String email;
	
}
