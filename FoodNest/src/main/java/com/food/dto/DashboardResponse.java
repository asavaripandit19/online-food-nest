package com.food.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DashboardResponse {

		private Integer todayOrders;

	    private BigDecimal todayRevenue;

	    private BigDecimal weeklyRevenue;

	    private BigDecimal monthlyRevenue;

	    private Integer activeOrders;

	    private Double averageRating;

	    private Integer totalReviews;

	    private Boolean isOnline;
}

