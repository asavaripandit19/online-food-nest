package com.food.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vendor_reviews")
@Getter
@Setter
public class VendorReview {

    // =========================
    // PRIMARY KEY (UUID)
    // =========================
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    // =========================
    // FOREIGN KEYS (UUID as per doc)
    // =========================
    @NotNull(message = "Vendor ID is required")
    @Column(nullable = false)
    private UUID vendorId;

    @NotNull(message = "Customer ID is required")
    @Column(nullable = false)
    private UUID customerId;

    @NotNull(message = "Order ID is required")
    @Column(nullable = false)
    private UUID orderId;

    // =========================
    // RATING
    // =========================
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be at least 1")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Double rating;

    // =========================
    // REVIEW TEXT
    // =========================
    @NotBlank(message = "Review text cannot be empty")
    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String reviewText;

    // =========================
    // CREATED TIME
    // =========================
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}