package com.food.service;

import java.util.UUID;

import com.food.dto.ToggleStatusRequest;


    public interface AvailabilityService {

        String toggleVendorStatus(UUID vendorId, ToggleStatusRequest request);
    }
