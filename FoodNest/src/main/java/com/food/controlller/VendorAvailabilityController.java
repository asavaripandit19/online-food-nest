package com.food.controlller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.dto.ToggleStatusRequest;
import com.food.security.CustomUserPrincipal;
import com.food.service.AvailabilityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vendor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VENDOR')")
public class VendorAvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/toggle-status")
    public ResponseEntity<?> toggleStatus(
            @Valid @RequestBody ToggleStatusRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new RuntimeException("Unauthorized");
        }

        UUID vendorId = principal.getUserId();
        String message = availabilityService.toggleVendorStatus(vendorId, request);

        return ResponseEntity.ok(message);
    }
}