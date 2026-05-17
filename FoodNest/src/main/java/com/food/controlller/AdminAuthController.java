package com.food.controlller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.food.dto.EmailOtpVerifyRequest;
import com.food.dto.LoginRequest;
import com.food.dto.MobileOtpVerifyRequest;
import com.food.dto.SendEmailOtpRequest;
import com.food.dto.SendMobileOtpRequest;
import com.food.dto.UsernamePasswordRequest;
import com.food.enums.OtpPurpose;
import com.food.enums.Role;
import com.food.service.AuthService;
import com.food.service.OtpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {

	@Autowired
	private OtpService otpService;

	@Autowired
	private AuthService authService;

	// =========================================================
	// SIGNUP - MOBILE SEND OTP
	// =========================================================

	@PostMapping("/signup/mobile/send-otp")
	public ResponseEntity<?> signupMobileSendOtp(@Valid @RequestBody SendMobileOtpRequest request) {

		return ResponseEntity.ok(

				authService.signupMobileSendOtp(request.getMobile(), Role.ADMIN));
	}

	// =========================================================
	// SIGNUP - MOBILE VERIFY OTP
	// =========================================================

	@PostMapping("/signup/mobile/verify-otp")
	public ResponseEntity<?> signupMobileVerifyOtp(@Valid @RequestBody MobileOtpVerifyRequest request) {

		return ResponseEntity.ok(

				otpService.verifyOtp(request.getMobile(), request.getOtp(), OtpPurpose.SIGNUP, Role.ADMIN));
	}

	// =========================================================
	// SIGNUP - EMAIL SEND OTP
	// =========================================================
	@PostMapping("/signup/email/send-otp")
	public ResponseEntity<?> signupEmailSendOtp(@Valid @RequestBody SendEmailOtpRequest request) {

		return ResponseEntity.ok(

				authService.signupEmailSendOtp(request.getEmail(), Role.ADMIN));
	}
	// =========================================================
	// SIGNUP - EMAIL VERIFY OTP
	// =========================================================

	@PostMapping("/signup/email/verify-otp")
	public ResponseEntity<?> signupEmailVerifyOtp(@Valid @RequestBody EmailOtpVerifyRequest request) {

		return ResponseEntity.ok(

				otpService.verifyEmailOtp(request.getEmail(), request.getOtp(), OtpPurpose.SIGNUP, Role.ADMIN));
	}

	@PostMapping("/signup/username/password")
	public ResponseEntity<?> signupWithUsernamePassword(@Valid @RequestBody UsernamePasswordRequest request) {

		return ResponseEntity.ok(

				authService.signupWithUsernamePassword(request, Role.ADMIN));
	}

	// =========================================================
	// LOGIN - MOBILE SEND OTP
	// =========================================================

	@PostMapping("/login/mobile/send-otp")
	public ResponseEntity<?> loginMobileSendOtp(@Valid @RequestBody SendMobileOtpRequest request) {

		return ResponseEntity.ok(

				authService.sendLoginMobileOtp(request.getMobile(), Role.ADMIN));
	}
	// =========================================================
	// LOGIN - MOBILE VERIFY OTP
	// =========================================================

	@PostMapping("/login/mobile/verify-otp")
	public ResponseEntity<?> loginMobileVerifyOtp(@Valid @RequestBody MobileOtpVerifyRequest request) {

		return ResponseEntity.ok(

				authService.loginVerifyMobileOtp(request.getMobile(), request.getOtp()));
	}

	@PostMapping("/login/email/send-otp")
	public ResponseEntity<?> loginEmailSendOtp(@Valid @RequestBody SendEmailOtpRequest request) {

		return ResponseEntity.ok(

				authService.sendLoginEmailOtp(request.getEmail(), Role.ADMIN));
	}

	// =========================================================
	// LOGIN - EMAIL VERIFY OTP
	// =========================================================

	@PostMapping("/login/email/verify-otp")
	public ResponseEntity<?> loginEmailVerifyOtp(@Valid @RequestBody EmailOtpVerifyRequest request) {

		return ResponseEntity.ok(

				authService.verifyEmailLoginOtp(request.getEmail(), request.getOtp()));
	}

	// =========================================================
	// LOGIN - USERNAME PASSWORD
	// =========================================================

	@PostMapping("/login/username/password")
	public ResponseEntity<?> loginWithUsernamePassword(@Valid @RequestBody LoginRequest request) {

		return ResponseEntity.ok(

				authService.login(request));
	}
}