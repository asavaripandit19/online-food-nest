package com.food.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.food.model.VendorAvailability;

@Repository
public interface VendorAvailabilityRepository
        extends JpaRepository<VendorAvailability, UUID> {

    Optional<VendorAvailability> findByVendorId(UUID vendorId);
}