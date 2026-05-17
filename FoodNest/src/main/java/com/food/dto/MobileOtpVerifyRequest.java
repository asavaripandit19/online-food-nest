package com.food.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MobileOtpVerifyRequest {

    @NotBlank(message = "Mobile number is required")
    
    @Pattern(
            regexp = "^\\+91[6-9]\\d{9}$",
            message = "Invalid mobile format"
    )
    
    private String mobile;

    
    @NotBlank(message = "OTP is required")
    
    @Pattern(
            regexp = "^\\d{6}$",
            message = "OTP must be 6 digits"
    )
    
    private String otp;

}