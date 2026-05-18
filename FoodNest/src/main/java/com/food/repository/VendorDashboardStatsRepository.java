package com.food.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.food.model.VendorDashboardStats;

@Repository
public interface VendorDashboardStatsRepository
        extends JpaRepository<VendorDashboardStats, UUID> {

    Optional<VendorDashboardStats> findByVendorId(UUID vendorId);
}