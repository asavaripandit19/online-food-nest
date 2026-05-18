package com.food.controlller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.food.dto.DashboardResponse;
import com.food.security.CustomUserPrincipal;
import com.food.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/vendor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VENDOR')")
public class VendorDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {

        // =========================
        // GET LOGGED IN USER
        // =========================
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Unauthorized");
        }

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        // =========================
        // FIX: UUID instead of Long
        // =========================
        UUID vendorId = principal.getUserId();

        // =========================
        // SERVICE CALL
        // =========================
        DashboardResponse response =
                dashboardService.getVendorDashboard(vendorId);

        return ResponseEntity.ok(response);
    }
}