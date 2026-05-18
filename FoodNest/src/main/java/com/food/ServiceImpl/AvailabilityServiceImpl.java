package com.food.ServiceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.food.dto.ToggleStatusRequest;
import com.food.model.VendorAvailability;
import com.food.repository.VendorAvailabilityRepository;
import com.food.service.AvailabilityService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailabilityServiceImpl implements AvailabilityService {

    private final VendorAvailabilityRepository repository;

    @Override
    @CacheEvict(value = "vendor-dashboard", key = "#vendorId")
    public String toggleVendorStatus(UUID vendorId, ToggleStatusRequest request) {

        // 1. Validate vendorId
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }

        // 2. Validate request
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        // 3. Validate online status
        if (request.getIsOnline() == null) {
            throw new IllegalArgumentException("Online status is required");
        }

        // 4. Fetch or create entity
        VendorAvailability availability =
                repository.findByVendorId(vendorId)
                        .orElseGet(() -> {
                            VendorAvailability v = new VendorAvailability();
                            v.setVendorId(vendorId);
                            return v;
                        });

        // 5. Update values
        availability.setIsOnline(request.getIsOnline());
        availability.setUpdatedAt(LocalDateTime.now());

        // 6. Save
        repository.save(availability);

        // 7. Response
        return request.getIsOnline()
                ? "Vendor is ONLINE"
                : "Vendor is OFFLINE";
    }
}