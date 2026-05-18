package com.food.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ToggleStatusRequest {

	@NotNull(message = "Online status is required")
	private Boolean isOnline;

}
