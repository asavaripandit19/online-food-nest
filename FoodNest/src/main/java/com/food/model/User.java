package com.food.model;

import java.time.LocalDateTime;

import com.food.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="users")

public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	// ==========================================
	// USERNAME
	// ==========================================
	
	@Column(unique = true)
	private String username;
	
	
	// ==========================================
	// MOBILE
	// ==========================================
	
	@Column(unique = true)
	private String mobile;
	
	
	// ==========================================
	// VERIFIED
	// ==========================================
	
	@Column(columnDefinition = "TINYINT(1)")
	private boolean verified;
	
	
	// ==========================================
	// ACTIVE
	// ==========================================
	
	@Column(columnDefinition = "TINYINT(1)")
	private boolean active;
	
	
	// ==========================================
	// EMAIL
	// ==========================================
	
	@Column(unique = true)
	private String email;
	
	
	// ==========================================
	// PASSWORD
	// ==========================================
	
	private String password;
	
	
	// ==========================================
	// ROLE
	// ==========================================
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	
	// ==========================================
	// CREATED DATE
	// ==========================================
	
	private LocalDateTime createdAt;

}