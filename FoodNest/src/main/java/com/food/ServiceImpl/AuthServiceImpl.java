package com.food.ServiceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.food.dto.LoginRequest;
import com.food.dto.SignupRequest;
import com.food.enums.OtpPurpose;
import com.food.enums.Role;
import com.food.model.OtpVerification;
import com.food.model.User;
import com.food.repository.OtpRepository;
import com.food.repository.UserRepository;
import com.food.service.AuthService;
import com.food.service.JwtTokenService;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JwtTokenService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // SIGNUP MOBILE OTP VERIFY
    // =========================

    @Override
    public String verifySignupOtp(
            String mobile,
            String otp
    ) {

        validateMobile(mobile);

        OtpVerification verification =
                otpRepository
                .findTopByMobileAndPurposeOrderByIdDesc(
                        mobile,
                        OtpPurpose.SIGNUP
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        // expiry check
        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            return "OTP expired";
        }

        // attempt limit
        if (verification.getAttempts() >= 5) {

            return "Too many attempts";
        }

        // otp validation
        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            return "Incorrect OTP";
        }

        verification.setVerified(true);

        otpRepository.save(verification);

        return "Mobile OTP verified successfully";
    }

    // =========================
    // EMAIL OTP VERIFY
    // =========================

    @Override
    public String verifyEmailSignupOtp(
            String email,
            String otp
    ) {

        validateEmail(email);

        OtpVerification verification =
                otpRepository
                .findTopByEmailAndPurposeOrderByIdDesc(
                        email,
                        OtpPurpose.SIGNUP
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        // expiry check
        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            return "OTP expired";
        }

        // attempt limit
        if (verification.getAttempts() >= 5) {

            return "Too many attempts";
        }

        // otp validation
        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            return "Incorrect OTP";
        }

        verification.setVerified(true);

        otpRepository.save(verification);

        return "Email OTP verified successfully";
    }

    // =========================
    // SIGNUP
    // =========================

    @Override
    public String signup(SignupRequest request) {

        validateMobile(request.getMobile());

        validateEmail(request.getEmail());

        // email exists
        if (userRepository.existsByEmail(
                request.getEmail())) {

            return "Email already registered";
        }

        // mobile exists
        if (userRepository.existsByMobile(
                request.getMobile())) {

            return "Mobile already registered";
        }

        // mobile otp check
        OtpVerification mobileOtp =
                otpRepository
                .findTopByMobileAndPurposeOrderByIdDesc(
                        request.getMobile(),
                        OtpPurpose.SIGNUP
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mobile OTP not verified"
                        )
                );

        if (!mobileOtp.isVerified()) {

            return "Mobile OTP not verified";
        }

        // email otp check
        OtpVerification emailOtp =
                otpRepository
                .findTopByEmailAndPurposeOrderByIdDesc(
                        request.getEmail(),
                        OtpPurpose.SIGNUP
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email OTP not verified"
                        )
                );

        if (!emailOtp.isVerified()) {

            return "Email OTP not verified";
        }

        User user = new User();

        user.setEmail(request.getEmail());

        user.setMobile(request.getMobile());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(Role.VENDOR);

        user.setVerified(true);

        user.setActive(true);

        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "Signup successful";
    }

    // =========================
    // LOGIN WITH EMAIL
    // =========================

    @Override
    public String login(LoginRequest request) {

        validateEmail(request.getEmail());

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        // active check
        if (!user.isActive()) {

            return "Account disabled";
        }

        // password check
        boolean matched =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!matched) {

            return "Invalid email or password";
        }

        // generate jwt
        return jwtService.generateToken(
                user.getId(),
                user.getRole().name()
        );
    }

    // =========================
    // LOGIN OTP VERIFY
    // =========================

    @Override
    public String loginVerifyOtp(
            String mobile,
            String otp
    ) {

        validateMobile(mobile);

        OtpVerification verification =
                otpRepository
                .findTopByMobileAndPurposeOrderByIdDesc(
                        mobile,
                        OtpPurpose.LOGIN
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        // expiry check
        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            return "OTP expired";
        }

        // attempt limit
        if (verification.getAttempts() >= 5) {

            return "Too many attempts";
        }

        // otp check
        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            return "Incorrect OTP";
        }

        User user = userRepository
                .findByMobile(mobile)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        if (!user.isActive()) {

            return "Account disabled";
        }

        return jwtService.generateToken(
                user.getId(),
                user.getRole().name()
        );
    }

    // =========================
    // FORGOT PASSWORD SEND OTP
    // =========================

    @Override
    public String forgotPasswordSendOtp(
            String email
    ) {

        validateEmail(email);

        userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        String otp = String.valueOf(
                1000 + new SecureRandom()
                .nextInt(9000)
        );

        OtpVerification verification =
                new OtpVerification();

        verification.setEmail(email);

        verification.setOtp(otp);

        verification.setPurpose(
                OtpPurpose.FORGOT_PASSWORD
        );

        verification.setAttempts(0);

        verification.setVerified(false);

        verification.setCreatedAt(
                LocalDateTime.now()
        );

        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );

        otpRepository.save(verification);

        System.out.println(
                "FORGOT PASSWORD OTP = " + otp
        );

        return "OTP sent successfully";
    }

    // =========================
    // VERIFY FORGOT OTP
    // =========================

    @Override
    public String verifyForgotPasswordOtp(
            String email,
            String otp
    ) {

        validateEmail(email);

        OtpVerification verification =
                otpRepository
                .findTopByEmailAndPurposeOrderByIdDesc(
                        email,
                        OtpPurpose.FORGOT_PASSWORD
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        // expiry
        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            return "OTP expired";
        }

        // attempt limit
        if (verification.getAttempts() >= 5) {

            return "Too many attempts";
        }

        // otp check
        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            return "Incorrect OTP";
        }

        verification.setVerified(true);

        otpRepository.save(verification);

        return "OTP verified successfully";
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Override
    public String resetPassword(
            String email,
            String otp,
            String newPassword
    ) {

        validateEmail(email);

        OtpVerification verification =
                otpRepository
                .findTopByEmailAndPurposeOrderByIdDesc(
                        email,
                        OtpPurpose.FORGOT_PASSWORD
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "OTP not found"
                        )
                );

        // verified check
        if (!verification.isVerified()) {

            return "OTP not verified";
        }

        // otp match
        if (!verification.getOtp().equals(otp)) {

            return "Invalid OTP";
        }

        // expiry check
        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            return "OTP expired";
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);

        return "Password reset successful";
    }

    // =========================
    // MOBILE VALIDATION
    // =========================

    @Override
    public void validateMobile(String mobile) {

        if (mobile == null || mobile.isBlank()) {

            throw new RuntimeException(
                    "Mobile number required"
            );
        }

        if (!mobile.matches(
                "^\\+91[6-9]\\d{9}$"
        )) {

            throw new RuntimeException(
                    "Invalid mobile format"
            );
        }
    }

    // =========================
    // EMAIL VALIDATION
    // =========================

    @Override
    public void validateEmail(String email) {

        if (email == null || email.isBlank()) {

            throw new RuntimeException(
                    "Email required"
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

	@Override
	public String verifyEmailLoginOtp(String email, String otp) {
	

		    validateEmail(email);

		    OtpVerification verification =
		            otpRepository
		            .findTopByEmailAndPurposeOrderByIdDesc(
		                    email,
		                    OtpPurpose.LOGIN
		            )
		            .orElseThrow(() ->
		                    new RuntimeException(
		                            "OTP not found"
		                    )
		            );

		    // expiry check
		    if (verification.getExpiresAt()
		            .isBefore(LocalDateTime.now())) {

		        return "OTP expired";
		    }

		    // attempt limit
		    if (verification.getAttempts() >= 5) {

		        return "Too many attempts";
		    }

		    // otp validation
		    if (!verification.getOtp().equals(otp)) {

		        verification.setAttempts(
		                verification.getAttempts() + 1
		        );

		        otpRepository.save(verification);

		        return "Incorrect OTP";
		    }

		    verification.setVerified(true);

		    otpRepository.save(verification);

		    User user = userRepository
		            .findByEmail(email)
		            .orElseThrow(() ->
		                    new RuntimeException(
		                            "User not found"
		                    )
		            );

		    if (!user.isActive()) {

		        return "Account disabled";
		    }

		    return jwtService.generateToken(
		            user.getId(),
		            user.getRole().name()
		    );
		}
		
	}
