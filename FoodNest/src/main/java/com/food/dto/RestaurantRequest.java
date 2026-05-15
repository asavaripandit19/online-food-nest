package com.food.dto;

import java.util.List;

import com.food.enums.FoodType;
import com.food.enums.ServiceType;

//import com.food.enums.BusinessType;

import lombok.Data;

@Data
public class RestaurantRequest {

	private String restaurantName;
    private String ownerName;
    private String mobile;
    private String email;
//    private BusinessType businessType;
    private String businessType;

    private String logoUrl;
    private String coverImageUrl;

    private String openTime;
    private String closeTime;
    
    private FoodType foodType;

    private List<String> cuisines;

    private List<ServiceType> serviceTypes;
}
