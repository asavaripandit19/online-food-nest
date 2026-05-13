package com.food.controlller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.food.dto.*;
import com.food.enums.OtpPurpose;
import com.food.enums.Role;
import com.food.service.AuthService;
import com.food.service.OtpService;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    // =========================
    // MOBILE SIGNUP OTP
    // =========================

    @PostMapping("/signup/mobile/send-otp")
    public String sendMobileOtp(@RequestBody SendOtpRequest request) {
        return otpService.generateAndSaveOtp(request.getMobile(), OtpPurpose.SIGNUP);
    }

    @PostMapping("/signup/mobile/verify-otp")
    public String verifyMobileOtp(@RequestBody VerifyOtpRequest request) {
        return authService.verifySignupOtp(request.getMobile(), request.getOtp());
    }

    // =========================
    // EMAIL SIGNUP OTP
    // =========================

    @PostMapping("/signup/email/send-otp")
    public String sendEmailOtp(@RequestBody SendOtpRequest request) {
        return otpService.sendEmailOtp(request.getEmail(), OtpPurpose.SIGNUP);
    }

    @PostMapping("/signup/email/verify-otp")
    public String verifyEmailOtp(@RequestBody VerifyOtpRequest request) {
        return authService.verifyEmailSignupOtp(request.getEmail(), request.getOtp());
    }

    // =========================
    // SIGNUP ADMIN ACCOUNT
    // =========================

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        return authService.signup(request, Role.ADMIN);
    }

    // =========================
    // LOGIN (EMAIL PASSWORD)
    // =========================

    @PostMapping("/login/email/password")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // =========================
    // LOGIN MOBILE OTP
    // =========================

    @PostMapping("/login/mobile/send-otp")
    public String sendLoginOtp(@RequestBody SendOtpRequest request) {
        return otpService.generateAndSaveOtp(request.getMobile(), OtpPurpose.LOGIN);
    }

    @PostMapping("/login/mobile/verify-otp")
    public String verifyLoginOtp(@RequestBody VerifyOtpRequest request) {
        return authService.loginVerifyOtp(request.getMobile(), request.getOtp());
    }

    // =========================
    // LOGIN EMAIL OTP
    // =========================

    @PostMapping("/login/email/send-otp")
    public String sendEmailLoginOtp(@RequestBody SendOtpRequest request) {
        return otpService.sendEmailOtp(request.getEmail(), OtpPurpose.LOGIN);
    }

    @PostMapping("/login/email/verify-otp")
    public String verifyEmailLoginOtp(@RequestBody VerifyOtpRequest request) {
        return authService.verifyEmailLoginOtp(request.getEmail(), request.getOtp());
    }
}