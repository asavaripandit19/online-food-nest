package com.food.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.Data;

@Entity
@Table(name = "vendor_dashboard_stats")
@Data
public class VendorDashboardStats {

    // =========================
    // ID (UUID)
    // =========================
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false, updatable = false)
    private UUID id;

    // =========================
    // VENDOR ID (UUID as per doc)
    // =========================
    @NotNull(message = "Vendor ID is required")
    @Column(nullable = false, unique = true)
    private UUID vendorId;

    // =========================
    // STATS
    // =========================
    @Min(value = 0)
    @Column(nullable = false)
    private Integer todayOrders = 0;

    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal todayRevenue = BigDecimal.ZERO;

    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal weeklyRevenue = BigDecimal.ZERO;

    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRevenue = BigDecimal.ZERO;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer activeOrdersCount = 0;

    @DecimalMin(value = "0.0")
    @Max(value = 5)
    @Column(nullable = false)
    private Double avgRating = 0.0;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer totalReviews = 0;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isOnline = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}