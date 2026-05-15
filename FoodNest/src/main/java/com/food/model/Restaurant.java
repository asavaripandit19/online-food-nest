package com.food.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.food.enums.BusinessType;
import com.food.enums.FoodType;
import com.food.enums.ServiceType;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity

public class Restaurant {
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
	 	private Long id;

	    private String restaurantName;
	    private String ownerName;
	    private String mobile;
	    private String email;

//	    @Enumerated(EnumType.STRING)
//	    private BusinessType businessType;
	    
	    
	    private String businessType;

	    private String logoUrl;
	    private String coverImageUrl;

	    private LocalTime openTime;
	    private LocalTime closeTime;

	    private String restaurantCode; // GENERATED ID

	    private LocalDateTime createdAt;
	    
	    @Enumerated(EnumType.STRING)
	    private FoodType foodType;

	    @ElementCollection
	    private List<String> cuisines;

	    @ElementCollection
	    @Enumerated(EnumType.STRING)
	    private List<ServiceType> serviceTypes;
	    
}
