package com.food.controlller;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.dto.OrderRequest;
import com.food.enums.OrderStatus;
import com.food.model.Order;
import com.food.model.User;
import com.food.repository.OrderRepository;
import com.food.repository.UserRepository;
import com.food.security.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequest request,
            Authentication authentication) {

        CustomUserPrincipal user =
                (CustomUserPrincipal) authentication.getPrincipal();

        UUID vendorId = user.getUserId(); // from JWT token

        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Order order = new Order();
        order.setVendor(vendor);
        order.setCustomerId(request.getCustomerId());
        order.setTotalAmount(request.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return ResponseEntity.ok("Order created successfully");
    }
}