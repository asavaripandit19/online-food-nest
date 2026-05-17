package com.food.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsernamePasswordRequest {
	   @NotBlank(message = "Username is required")
	    private String username;

	    @NotBlank(message = "Password is required")
	    private String password;

	    @NotBlank(message = "Retype password is required")
	    private String retypePassword;

}