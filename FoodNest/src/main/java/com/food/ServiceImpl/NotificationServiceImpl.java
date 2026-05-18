package com.food.ServiceImpl;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.food.dto.OrderUpdateMessage;
import com.food.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // =========================
    // VENDOR NOTIFICATION
    // =========================
    @Override
    public void sendToVendor(OrderUpdateMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/vendor/" + message.getVendorId(),
                message
        );
    }

    // =========================
    // CUSTOMER NOTIFICATION
    // =========================
    @Override
    public void sendToCustomer(OrderUpdateMessage message) {

        messagingTemplate.convertAndSend(
                "/topic/customer/" + message.getCustomerId(),
                message
        );
    }
}