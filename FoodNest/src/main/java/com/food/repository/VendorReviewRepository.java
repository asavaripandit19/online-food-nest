package com.food.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.food.model.VendorReview;

@Repository
public interface VendorReviewRepository extends JpaRepository<VendorReview, UUID> {

    List<VendorReview> findByVendorId(UUID vendorId);
}