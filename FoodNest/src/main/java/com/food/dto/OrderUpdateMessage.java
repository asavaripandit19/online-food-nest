package com.food.dto;

import java.util.UUID;

import com.food.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderUpdateMessage {

		private Long orderId;
	    private UUID vendorId;
	    private Long customerId;
	    private OrderStatus status;
	    private String message;
}
