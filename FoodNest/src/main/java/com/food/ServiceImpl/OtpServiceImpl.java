package com.food.ServiceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.food.enums.OtpPurpose;
import com.food.model.OtpVerification;
import com.food.repository.OtpRepository;
import com.food.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    private static final SecureRandom secureRandom =
            new SecureRandom();

    @Override
    public String generateAndSaveOtp(
            String mobile,
            OtpPurpose purpose
    ) {
    	
    	validateMobile(mobile);

        // ✅ Validation
        if (mobile == null || mobile.isBlank()) {
            throw new RuntimeException("Mobile number required");
        }

        // ✅ Find latest OTP
        Optional<OtpVerification> existingOtp =
                otpRepository
                .findTopByMobileAndPurposeOrderByIdDesc(
                        mobile,
                        purpose
                );

        if (existingOtp.isPresent()) {

            OtpVerification oldOtp = existingOtp.get();

            // ✅ Resend cooldown (30 sec)
            if (oldOtp.getCreatedAt() != null
                    && oldOtp.getCreatedAt()
                    .plusSeconds(30)
                    .isAfter(LocalDateTime.now())) {

                throw new RuntimeException(
                        "Please wait 30 seconds before requesting another OTP"
                );
            }

            // ✅ Expired OTP cleanup
            if (oldOtp.getExpiresAt() != null
                    && oldOtp.getExpiresAt()
                    .isBefore(LocalDateTime.now())) {

                oldOtp.setVerified(true);

                otpRepository.save(oldOtp);
            }
        }

        // ✅ Generate secure OTP
        String otp = String.valueOf(
                1000 + secureRandom.nextInt(9000)
        );

        // ✅ Save OTP
        OtpVerification verification =
                new OtpVerification();

        verification.setMobile(mobile);

        verification.setOtp(otp);

        verification.setPurpose(purpose);

        verification.setAttempts(0);

        verification.setVerified(false);

        verification.setCreatedAt(
                LocalDateTime.now()
        );

        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );

        otpRepository.save(verification);

        System.out.println("OTP SAVED = " + otp);

        return otp;
    }

    @Override
    public String sendEmailOtp(
            String email,
            OtpPurpose purpose
    ) {
    	
    	validateEmail(email);

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email required");
        }

        String otp = String.valueOf(
                1000 + secureRandom.nextInt(9000)
        );

        OtpVerification verification =
                new OtpVerification();

        verification.setEmail(email);

        verification.setOtp(otp);

        verification.setPurpose(purpose);

        verification.setAttempts(0);

        verification.setVerified(false);

        verification.setCreatedAt(
                LocalDateTime.now()
        );

        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );

        otpRepository.save(verification);

        System.out.println("EMAIL OTP = " + otp);

        return "OTP sent successfully";
    }
    
    private void validateMobile(String mobile) {

        if (mobile == null || mobile.isBlank()) {

            throw new RuntimeException(
                    "Mobile number required"
            );
        }

        if (!mobile.matches("^\\+91[6-9]\\d{9}$")) {

            throw new RuntimeException(
                    "Invalid mobile number. Use format: +919876543210"
            );
        }
    }
    
	@Override
	public  void validateEmail(String email) {

	    if (email == null || email.isBlank()) {

	        throw new RuntimeException(
	                "Email is required"
	        );
	    }

	    if (!email.matches(
	            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
	    )) {

	        throw new RuntimeException(
	                "Invalid email format"
	        );
	    }
	}
}