package com.food.repository;



import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.food.enums.OrderStatus;
import com.food.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // =========================
    // TODAY ORDERS COUNT
    // =========================
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.vendor.id = :vendorId
        AND FUNCTION('date', o.createdAt) = CURRENT_DATE
    """)
    Integer getTodayOrders(@Param("vendorId") UUID vendorId);

    // =========================
    // TODAY REVENUE
    // =========================
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.vendor.id = :vendorId
        AND o.status = com.food.enums.OrderStatus.DELIVERED
        AND FUNCTION('date', o.createdAt) = CURRENT_DATE
    """)
    BigDecimal getTodayRevenue(@Param("vendorId") UUID vendorId);

    // =========================
    // ACTIVE ORDERS
    // =========================
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.vendor.id = :vendorId
        AND o.status IN (
            com.food.enums.OrderStatus.PENDING,
            com.food.enums.OrderStatus.ACCEPTED,
            com.food.enums.OrderStatus.PREPARING,
            com.food.enums.OrderStatus.READY_FOR_PICKUP
        )
    """)
    Integer getActiveOrders(@Param("vendorId") UUID vendorId);

    // =========================
    // FIND BY STATUS
    // =========================
   
}