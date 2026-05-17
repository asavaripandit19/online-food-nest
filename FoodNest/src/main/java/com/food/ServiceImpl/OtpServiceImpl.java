package com.food.ServiceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.food.enums.OtpPurpose;
import com.food.enums.Role;
import com.food.model.OtpVerification;
import com.food.model.User;
import com.food.repository.OtpRepository;
import com.food.repository.UserRepository;
import com.food.service.OtpService;

@Transactional
@Service
public class OtpServiceImpl implements OtpService {

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final SecureRandom secureRandom = new SecureRandom();

	// =====================================================
	// MOBILE OTP SEND
	// =====================================================

	@Override
	public String generateAndSaveOtp(String mobile, OtpPurpose purpose) {

		validateMobile(mobile);

		Optional<OtpVerification> existingOtp = otpRepository.findTopByMobileAndPurposeOrderByIdDesc(mobile, purpose);

		if (existingOtp.isPresent()) {

			OtpVerification oldOtp = existingOtp.get();

			if (oldOtp.getCreatedAt() != null && oldOtp.getCreatedAt().plusSeconds(30).isAfter(LocalDateTime.now())) {

				throw new RuntimeException("Please wait 30 seconds before requesting another OTP");
			}
		}

		String otp = String.format("%06d", secureRandom.nextInt(1000000));

		String hashedOtp = passwordEncoder.encode(otp);

		OtpVerification verification = new OtpVerification();

		verification.setMobile(mobile);

		verification.setOtp(hashedOtp);

		verification.setPurpose(purpose);

		verification.setAttempts(0);

		verification.setVerified(false);

		verification.setCreatedAt(LocalDateTime.now());

		verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

		otpRepository.save(verification);

		System.out.println("================================");
		System.out.println("REAL OTP = " + otp);
		System.out.println("MOBILE = " + mobile);
		System.out.println("PURPOSE = " + purpose);
		System.out.println("================================");

		return "OTP sent successfully";
	}

	// =====================================================
	// MOBILE OTP VERIFY
	// =====================================================

	@Override
	public String verifyOtp(String mobile, String otp, OtpPurpose purpose, Role role) {

		OtpVerification verification = otpRepository
				.findTopByMobileAndPurposeAndVerifiedFalseOrderByIdDesc(mobile, purpose)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {

			throw new RuntimeException("OTP expired");
		}

		if (verification.getAttempts() >= 5) {

			throw new RuntimeException("Maximum OTP attempts exceeded");
		}

		boolean matches = passwordEncoder.matches(otp.trim(), verification.getOtp());

		if (!matches) {

			verification.setAttempts(verification.getAttempts() + 1);

			otpRepository.save(verification);

			throw new RuntimeException("Incorrect OTP");
		}

		// =============================
		// SUCCESS
		// =============================

		verification.setVerified(true);

		otpRepository.save(verification);

		// =============================
		// CREATE USER AFTER SIGNUP
		// =============================

		if (purpose == OtpPurpose.SIGNUP) {

			boolean exists = userRepository.existsByMobile(mobile);

			if (!exists) {

				User user = new User();

				user.setMobile(mobile);

				user.setVerified(true);

				user.setActive(true);

				user.setRole(role != null ? role : Role.VENDOR);

				user.setCreatedAt(LocalDateTime.now());

				userRepository.save(user);
			}
		}
		return "OTP verified successfully";
	}

	// =====================================================
	// EMAIL OTP SEND
	// =====================================================

	@Override
	public String sendEmailOtp(String email, OtpPurpose purpose) {

		validateEmail(email);

		Optional<OtpVerification> existingOtp = otpRepository.findTopByEmailAndPurposeOrderByIdDesc(email, purpose);

		if (existingOtp.isPresent()) {

			OtpVerification oldOtp = existingOtp.get();

			if (oldOtp.getCreatedAt() != null && oldOtp.getCreatedAt().plusSeconds(30).isAfter(LocalDateTime.now())) {

				throw new RuntimeException("Please wait 30 seconds before requesting another OTP");
			}
		}

		String otp = String.valueOf(100000 + secureRandom.nextInt(900000));

		String hashedOtp = passwordEncoder.encode(otp);

		OtpVerification verification = new OtpVerification();

		verification.setEmail(email);

		verification.setOtp(hashedOtp);

		verification.setPurpose(purpose);

		verification.setVerified(false);

		verification.setAttempts(0);

		verification.setCreatedAt(LocalDateTime.now());

		verification.setExpiresAt(LocalDateTime.now().plusMinutes(5));

		otpRepository.save(verification);

		System.out.println("================================");
		System.out.println("EMAIL OTP = " + otp);
		System.out.println("EMAIL = " + email);
		System.out.println("PURPOSE = " + purpose);
		System.out.println("================================");

		return "Email OTP sent successfully";
	}

	// =====================================================
	// EMAIL OTP VERIFY
	// =====================================================

	@Override
	public String verifyEmailOtp(String email, String otp, OtpPurpose purpose, Role role) {

		OtpVerification verification = otpRepository
				.findTopByEmailAndPurposeAndVerifiedFalseOrderByIdDesc(email, purpose)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {

			throw new RuntimeException("OTP expired");
		}

		if (verification.getAttempts() >= 5) {

			throw new RuntimeException("Maximum OTP attempts exceeded");
		}

		boolean isValid = passwordEncoder.matches(otp.trim(), verification.getOtp());

		if (!isValid) {

			verification.setAttempts(verification.getAttempts() + 1);

			otpRepository.save(verification);

			throw new RuntimeException("Incorrect OTP");
		}

		// =============================
		// SUCCESS
		// =============================

		verification.setVerified(true);

		otpRepository.save(verification);

		// =============================
		// CREATE USER AFTER EMAIL SIGNUP
		// =============================

		if (purpose == OtpPurpose.SIGNUP) {

			boolean exists = userRepository.existsByEmail(email);

			if (!exists) {

				User user = new User();

				user.setEmail(email);

				user.setVerified(true);

				user.setActive(true);

				user.setRole(role != null ? role : Role.VENDOR);

				user.setCreatedAt(LocalDateTime.now());

				userRepository.save(user);
			}
		}

		return "Email OTP verified successfully";
	}

	// =====================================================
	// MOBILE VALIDATION
	// =====================================================

	private void validateMobile(String mobile) {

		if (mobile == null || mobile.isBlank()) {

			throw new RuntimeException("Mobile number required");
		}

		if (!mobile.matches("^\\+91[6-9]\\d{9}$")) {

			throw new RuntimeException("Invalid mobile number. Use format: +919876543210");
		}
	}

	// =====================================================
	// EMAIL VALIDATION
	// =====================================================

	@Override
	public void validateEmail(String email) {

		if (email == null || email.isBlank()) {

			throw new RuntimeException("Email is required");
		}

		if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

			throw new RuntimeException("Invalid email format");
		}
	}
}