package com.food.ServiceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.food.dto.LoginRequest;
import com.food.dto.UsernamePasswordRequest;
import com.food.enums.OtpPurpose;
import com.food.enums.Role;
import com.food.model.OtpVerification;
import com.food.model.User;
import com.food.repository.OtpRepository;
import com.food.repository.UserRepository;
import com.food.service.AuthService;
import com.food.service.JwtTokenService;
import com.food.service.OtpService;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenService jwtService;

	@Autowired
	private OtpService otpService;

	// =====================================================
	// MOBILE NORMALIZATION (COMMON)
	// =====================================================
	private String normalizeMobile(String mobile) {
		if (mobile == null)
			return null;

		mobile = mobile.replaceAll("\\s+", "").replaceAll("-", "");

		if (mobile.matches("^91[6-9]\\d{9}$")) {
			mobile = "+" + mobile;
		}

		return mobile;
	}

	// =====================================================
	// VALIDATE MOBILE
	// =====================================================
	@Override
	public void validateMobile(String mobile) {

		if (mobile == null || mobile.isBlank()) {
			throw new RuntimeException("Mobile is required");
		}

		if (!mobile.matches("^\\+91[6-9]\\d{9}$")) {
			throw new RuntimeException("Invalid mobile format");
		}
	}

	// =====================================================
	// VALIDATE EMAIL
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

	@Override
	public String signupMobileSendOtp(String mobile, Role role) {

		mobile = normalizeMobile(mobile);

		validateMobile(mobile);

		// check already exists with same role
		User user = userRepository.findByMobile(mobile).orElse(null);

		if (user != null && user.getRole() == role) {

			throw new RuntimeException(role.name() + " already registered with this mobile");
		}

		return otpService.generateAndSaveOtp(mobile, OtpPurpose.SIGNUP);
	}

	@Override
	public String signupEmailSendOtp(String email, Role role) {

		validateEmail(email);

		// check already exists with same role
		User user = userRepository.findByEmail(email).orElse(null);

		if (user != null && user.getRole() == role) {

			throw new RuntimeException(role.name() + " already registered with this email");
		}

		return otpService.sendEmailOtp(email, OtpPurpose.SIGNUP);
	}

	// =====================================================
	// SIGNUP MOBILE OTP VERIFY
	// =====================================================
	@Override
	public String verifyMobileOtp(String mobile, String otp) {

		mobile = normalizeMobile(mobile);
		validateMobile(mobile);

		OtpVerification verification = otpRepository.findTopByMobileAndPurposeOrderByIdDesc(mobile, OtpPurpose.SIGNUP)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
			return "OTP expired";
		}

		boolean isValidOtp = passwordEncoder.matches(otp, verification.getOtp());

		if (!isValidOtp) {
			verification.setAttempts(verification.getAttempts() + 1);
			otpRepository.save(verification);
			return "Invalid OTP";
		}

		verification.setVerified(true);
		otpRepository.save(verification);

		return "OTP verified successfully";
	}

	// =====================================================
	// SIGNUP EMAIL OTP VERIFY
	// =====================================================
	@Override
	public String verifyEmailOtp(String email, String otp) {

		validateEmail(email);

		OtpVerification verification = otpRepository.findTopByEmailAndPurposeOrderByIdDesc(email, OtpPurpose.SIGNUP)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
			return "OTP expired";
		}

		boolean isValidOtp = passwordEncoder.matches(otp, verification.getOtp());

		if (!isValidOtp) {
			verification.setAttempts(verification.getAttempts() + 1);
			otpRepository.save(verification);
			return "Invalid OTP";
		}

		verification.setVerified(true);
		otpRepository.save(verification);

		return "OTP verified successfully";
	}

	// =====================================================
	// SIGNUP USERNAME + PASSWORD
	// =====================================================
	@Override
	public String signupWithUsernamePassword(UsernamePasswordRequest request, Role role) {

		if (!request.getPassword().equals(request.getRetypePassword())) {

			return "Password and Retype Password do not match";
		}

		if (userRepository.findByUsername(request.getUsername()).isPresent()) {

			return "Username already exists";
		}

		User user = new User();

		user.setUsername(request.getUsername());

		user.setPassword(passwordEncoder.encode(request.getPassword()));

		// =========================
		// ROLE FROM CONTROLLER
		// =========================
		user.setRole(role);

		user.setActive(true);

		user.setVerified(true);

		user.setCreatedAt(LocalDateTime.now());

		userRepository.save(user);

		return role.name() + " signup successful";
	}

	// =====================================================
	// SEND LOGIN MOBILE OTP
	// =====================================================

	@Override
	public String sendLoginMobileOtp(String mobile, Role role) {

		mobile = normalizeMobile(mobile);

		validateMobile(mobile);

		User user = userRepository.findByMobile(mobile)
				.orElseThrow(() -> new RuntimeException("User not registered. Please signup first."));

		if (!user.isActive()) {
			return "Account disabled";
		}

		if (user.getRole() != role) {
			throw new RuntimeException("Access denied for " + role.name());
		}

		return otpService.generateAndSaveOtp(mobile, OtpPurpose.LOGIN);
	}

	// =====================================================
	// SEND LOGIN EMAIL OTP
	// =====================================================

	@Override
	public String sendLoginEmailOtp(String email, Role role) {

		validateEmail(email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not registered. Please signup first."));

		if (!user.isActive()) {
			return "Account disabled";
		}

		if (user.getRole() != role) {
			throw new RuntimeException("Access denied for " + role.name());
		}

		return otpService.sendEmailOtp(email, OtpPurpose.LOGIN);
	}

	// =====================================================
	// LOGIN USERNAME + PASSWORD
	// =====================================================
	@Override
	public String login(LoginRequest request) {

		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!user.isActive()) {
			return "Account disabled";
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return "Invalid username or password";
		}

		return jwtService.generateToken(
		        user.getId(),
		        user.getEmail(),
		        user.getRole().name()
		);
	}

	// =====================================================
	// LOGIN MOBILE OTP VERIFY
	// =====================================================
	@Override
	public String loginVerifyMobileOtp(String mobile, String otp) {

		mobile = normalizeMobile(mobile);
		validateMobile(mobile);

		User user = userRepository.findByMobile(mobile)
				.orElseThrow(() -> new RuntimeException("Mobile number not registered. Please signup first."));

		if (!user.isActive()) {
			return "Account disabled";
		}

		OtpVerification verification = otpRepository.findTopByMobileAndPurposeOrderByIdDesc(mobile, OtpPurpose.LOGIN)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
			return "OTP expired";
		}

		boolean isValidOtp = passwordEncoder.matches(otp, verification.getOtp());

		if (!isValidOtp) {
			verification.setAttempts(verification.getAttempts() + 1);
			otpRepository.save(verification);
			return "Invalid OTP";
		}

		verification.setVerified(true);
		otpRepository.save(verification);

		return jwtService.generateToken(
		        user.getId(),
		        user.getEmail(),
		        user.getRole().name()
		);
	}

	// =====================================================
	// LOGIN EMAIL OTP VERIFY
	// =====================================================
	@Override
	public String verifyEmailLoginOtp(String email, String otp) {

		validateEmail(email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Email not registered. Please signup first."));

		if (!user.isActive()) {
			return "Account disabled";
		}

		OtpVerification verification = otpRepository.findTopByEmailAndPurposeOrderByIdDesc(email, OtpPurpose.LOGIN)
				.orElseThrow(() -> new RuntimeException("OTP not found"));

		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
			return "OTP expired";
		}

		boolean isValidOtp = passwordEncoder.matches(otp, verification.getOtp());

		if (!isValidOtp) {
			verification.setAttempts(verification.getAttempts() + 1);
			otpRepository.save(verification);
			return "Invalid OTP";
		}

		verification.setVerified(true);
		otpRepository.save(verification);

		   return jwtService.generateToken(
		            user.getId(),     // UUID now
		            user.getEmail(),
		            user.getRole().name()
		    );
	}
}