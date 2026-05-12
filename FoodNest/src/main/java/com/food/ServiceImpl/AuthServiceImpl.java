package com.food.ServiceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.food.dto.LoginRequest;
import com.food.dto.SignupRequest;
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
	
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Override
	public String verifySignupOtp(String mobile, String otp) {
		
		validateMobile(mobile);
		

		
		OtpVerification verification = otpRepository.findTopByMobileOrderByIdDesc(mobile)
				.orElseThrow(() -> new RuntimeException("OTP Not Found!!"));
		
		
		if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "OTP expired";
        }

        if (!verification.getOtp().equals(otp)) {
            verification.setAttempts(verification.getAttempts() + 1);
            otpRepository.save(verification);
            return "Incorrect OTP";
        }

        verification.setVerified(true);
        otpRepository.save(verification);

        verification.setVerified(true);
        otpRepository.save(verification);

        return "OTP verified successfully";

      
		
	}

	

	@Override
	public String login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return "Email or password incorrect";
        }

        return jwtService.generateToken(user.getId(), user.getRole().name());
	}

	@Override
	public String loginVerifyOtp(String mobile, String otp) {
		validateMobile(mobile);
		 OtpVerification verification = otpRepository
	                .findTopByMobileOrderByIdDesc(mobile)
	                .orElseThrow(() -> new RuntimeException("OTP not found"));

	        if (!verification.getOtp().equals(otp)) {
	            return "Incorrect OTP";
	        }

	        User user = userRepository.findByMobile(mobile)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        return jwtService.generateToken(user.getId(), user.getRole().name());
	    
	}




		@Override
		public String signup(SignupRequest request) {

			validateMobile(request.getMobile());

		    // check email exists
		    if (userRepository.existsByEmail(request.getEmail())) {
		        return "Email already registered";
		    }

		    // check mobile exists
		    if (userRepository.existsByMobile(request.getMobile())) {
		        return "Mobile already registered";
		    }

		    // check mobile otp verified
		    OtpVerification mobileOtp = otpRepository
		            .findTopByMobileOrderByIdDesc(
		                    request.getMobile())
		            .orElseThrow(() ->
		                    new RuntimeException(
		                            "Mobile OTP not verified"));

		    if (!mobileOtp.isVerified()) {
		        return "Mobile OTP not verified";
		    }

		    // create user
		    User user = new User();

		    user.setEmail(request.getEmail());
		    user.setMobile(request.getMobile());

		    user.setPassword(
		            encoder.encode(request.getPassword()));

		    user.setRole(Role.VENDOR);
		    user.setMobile(request.getMobile());
		    user.setVerified(true);
		    user.setActive(true);

		    user.setCreatedAt(LocalDateTime.now());

		    userRepository.save(user);

		    return "Signup successful";
		
	}

		@Override
		public String verifyEmailSignupOtp(String email, String otp) {

			validateEmail(email);
		    OtpVerification verification =
		            otpRepository.findTopByEmailOrderByIdDesc(email)
		            .orElseThrow(() -> new RuntimeException("OTP not found"));

		    if (!verification.getOtp().equals(otp)) {
		        return "Incorrect OTP";
		    }

		    verification.setVerified(true);
		    otpRepository.save(verification);

		    return "Email OTP verified successfully";
		}
		
		@Override
		public String forgotPasswordSendOtp(String email) {
			validateEmail(email);
		    // check user exists
		    User user = userRepository.findByEmail(email)
		            .orElseThrow(() ->
		                    new RuntimeException("User not found"));

		    // generate OTP
		    String otp = String.valueOf(
		            1000 + new java.security.SecureRandom()
		            .nextInt(9000)
		    );

		    // save OTP
		    OtpVerification verification =
		            new OtpVerification();

		    verification.setEmail(email);

		    verification.setOtp(otp);

		    verification.setPurpose(
		            com.food.enums.OtpPurpose.FORGOT_PASSWORD
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

		    System.out.println("FORGOT PASSWORD OTP = " + otp);

		    return "OTP sent successfully";
		}
		
		@Override
		public String verifyForgotPasswordOtp(
		        String email,
		        String otp
		) {

			validateEmail(email);
		    OtpVerification verification =
		            otpRepository
		            .findTopByEmailOrderByIdDesc(email)
		            .orElseThrow(() ->
		                    new RuntimeException("OTP not found"));

		    // expired
		    if (verification.getExpiresAt()
		            .isBefore(LocalDateTime.now())) {

		        return "OTP expired";
		    }

		    // invalid OTP
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
		
		@Override
		public String resetPassword(
		        String email,
		        String otp,
		        String newPassword
		) {
			validateEmail(email);
		    OtpVerification verification =
		            otpRepository
		            .findTopByEmailOrderByIdDesc(email)
		            .orElseThrow(() ->
		                    new RuntimeException("OTP not found"));

		    // OTP check
		    if (!verification.isVerified()) {
		        return "OTP not verified";
		    }

		    // user check
		    User user = userRepository.findByEmail(email)
		            .orElseThrow(() ->
		                    new RuntimeException("User not found"));

		    // password update
		    user.setPassword(
		            encoder.encode(newPassword)
		    );

		    userRepository.save(user);

		    return "Password reset successful";
		}
		
		@Override
		public void validateMobile(String mobile) {

		    if (mobile == null || mobile.isBlank()) {

		        throw new RuntimeException(
		                "Mobile number required"
		        );
		    }

		    // +91 followed by 10 digits
		    if (!mobile.matches("^\\+91[6-9]\\d{9}$")) {

		        throw new RuntimeException(
		                "Mobile number must start with +91 and contain 10 digits"
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
