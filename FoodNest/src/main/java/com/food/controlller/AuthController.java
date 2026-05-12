package com.food.controlller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.food.dto.*;
import com.food.enums.OtpPurpose;
import com.food.service.AuthService;
import com.food.service.OtpService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    // EMAIL SIGNUP OTP (optional if used)
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
    // CREATE ACCOUNT
    // =========================

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    // =========================
    // LOGIN (EMAIL)
    // =========================

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // =========================
    // LOGIN (MOBILE OTP)
    // =========================

    @PostMapping("/login/send-otp")
    public String sendLoginOtp(@RequestBody SendOtpRequest request) {
        return otpService.generateAndSaveOtp(request.getMobile(), OtpPurpose.LOGIN);
    }

    @PostMapping("/login/verify-otp")
    public String verifyLoginOtp(@RequestBody VerifyOtpRequest request) {
        return authService.loginVerifyOtp(request.getMobile(), request.getOtp());
    }
    
    @PostMapping("/forgot-password/send-otp")
    public String forgotPasswordOtp(
            @RequestParam String email
    ) {
        return authService
                .forgotPasswordSendOtp(email);
    }
    
    @PostMapping("/forgot-password/verify-otp")
    public String verifyForgotOtp(
            @RequestParam String email,
            @RequestParam String otp
    ) {
        return authService
                .verifyForgotPasswordOtp(email, otp);
    }
    
    @PostMapping("/forgot-password/reset")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword
    ) {
        return authService
                .resetPassword(
                        email,
                        otp,
                        newPassword
                );
    }
}