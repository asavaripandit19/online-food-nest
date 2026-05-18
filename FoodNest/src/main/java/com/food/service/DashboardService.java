package com.food.service;

import java.util.UUID;

import com.food.dto.DashboardResponse;

public interface DashboardService {

	 DashboardResponse getVendorDashboard(UUID vendorId);
}