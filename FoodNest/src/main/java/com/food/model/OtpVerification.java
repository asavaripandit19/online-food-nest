package com.food.model;

import java.time.LocalDateTime;

import com.food.enums.OtpPurpose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "Otp_table")
public class OtpVerification {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(length = 13)
	    private String mobile;

	    @Column(length = 100)
	    private String email;

	    @Column(nullable = false, length = 6)
	    private String otp;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private OtpPurpose purpose;

	    @Column(nullable = false)
	    private LocalDateTime expiresAt;

	    @Column(nullable = false)
	    private LocalDateTime createdAt;

	    @Column(nullable = false, columnDefinition = "TINYINT(1)")
	    private boolean verified = false;

	    @Column(nullable = false)
	    private int attempts = 0;
	    
	    
		
		
		
		
		
		
}
