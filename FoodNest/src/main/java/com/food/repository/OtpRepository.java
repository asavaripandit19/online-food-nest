package com.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food.enums.OtpPurpose;
import com.food.model.OtpVerification;

public interface OtpRepository
        extends JpaRepository<OtpVerification, Long> {

    // =====================================================
    // MOBILE
    // =====================================================

    Optional<OtpVerification>
    findTopByMobileOrderByIdDesc(
            String mobile
    );

    Optional<OtpVerification>
    findTopByMobileAndPurposeOrderByIdDesc(
            String mobile,
            OtpPurpose purpose
    );

    Optional<OtpVerification>
    findTopByMobileAndPurposeAndVerifiedFalseOrderByIdDesc(
            String mobile,
            OtpPurpose purpose
    );

    // =====================================================
    // EMAIL
    // =====================================================

    Optional<OtpVerification>
    findTopByEmailOrderByIdDesc(
            String email
    );

    Optional<OtpVerification>
    findTopByEmailAndPurposeOrderByIdDesc(
            String email,
            OtpPurpose purpose
    );

    Optional<OtpVerification>
    findTopByEmailAndPurposeAndVerifiedFalseOrderByIdDesc(
            String email,
            OtpPurpose purpose
    );
}