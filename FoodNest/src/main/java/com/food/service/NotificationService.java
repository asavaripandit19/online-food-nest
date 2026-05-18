package com.food.service;

import com.food.dto.OrderUpdateMessage;

public interface NotificationService {
	 void sendToVendor(OrderUpdateMessage message);

	    void sendToCustomer(OrderUpdateMessage message);
}
