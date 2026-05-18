package com.food.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.food.dto.DashboardResponse;
import com.food.model.VendorDashboardStats;
import com.food.repository.VendorDashboardStatsRepository;
import com.food.repository.VendorAvailabilityRepository;
import com.food.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VendorDashboardStatsRepository dashboardRepo;

    // ✅ ADD THIS (VERY IMPORTANT)
    private final VendorAvailabilityRepository availabilityRepository;

    @Override
    @Cacheable(value = "vendor-dashboard", key = "#vendorId", unless = "#result == null")
    public DashboardResponse getVendorDashboard(UUID vendorId) {

        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }

        VendorDashboardStats stats =
                dashboardRepo.findByVendorId(vendorId)
                        .orElseGet(() -> {
                            VendorDashboardStats newStats = new VendorDashboardStats();
                            newStats.setVendorId(vendorId);
                            newStats.setTodayOrders(0);
                            newStats.setTodayRevenue(BigDecimal.ZERO);
                            newStats.setWeeklyRevenue(BigDecimal.ZERO);
                            newStats.setMonthlyRevenue(BigDecimal.ZERO);
                            newStats.setActiveOrdersCount(0);
                            newStats.setAvgRating(0.0);
                            newStats.setTotalReviews(0);
                            newStats.setUpdatedAt(LocalDateTime.now());
                            return dashboardRepo.save(newStats);
                        });

        return mapToResponse(stats, vendorId);
    }

    private DashboardResponse mapToResponse(VendorDashboardStats stats, UUID vendorId) {

        DashboardResponse response = new DashboardResponse();

        response.setTodayOrders(stats.getTodayOrders() != null ? stats.getTodayOrders() : 0);
        response.setTodayRevenue(stats.getTodayRevenue() != null ? stats.getTodayRevenue() : BigDecimal.ZERO);
        response.setWeeklyRevenue(stats.getWeeklyRevenue() != null ? stats.getWeeklyRevenue() : BigDecimal.ZERO);
        response.setMonthlyRevenue(stats.getMonthlyRevenue() != null ? stats.getMonthlyRevenue() : BigDecimal.ZERO);
        response.setActiveOrders(stats.getActiveOrdersCount() != null ? stats.getActiveOrdersCount() : 0);
        response.setAverageRating(stats.getAvgRating() != null ? stats.getAvgRating() : 0.0);
        response.setTotalReviews(stats.getTotalReviews() != null ? stats.getTotalReviews() : 0);

        // ✅ FIXED ONLINE STATUS
        boolean isOnline =
                availabilityRepository.findByVendorId(vendorId)
                        .map(v -> v.getIsOnline())
                        .orElse(false);

        response.setIsOnline(isOnline);

        return response;
    }
}