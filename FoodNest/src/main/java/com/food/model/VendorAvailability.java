package com.food.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Table(name = "vendor_availability")
@Data
public class VendorAvailability {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull(message = "Vendor ID is required")
    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID vendorId;

    @NotNull(message = "Online status is required")
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isOnline = false;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}