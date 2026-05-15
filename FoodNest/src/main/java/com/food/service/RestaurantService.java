package com.food.service;


import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.food.dto.RestaurantRequest;
import com.food.exception.ApiResponse;
import com.food.model.Restaurant;

import lombok.Data;


public interface RestaurantService {

  

	ApiResponse uploadCover(Long id, MultipartFile coverImage) throws IOException;

	ApiResponse uploadLogo(Long id, MultipartFile logo) throws IOException;

	ApiResponse createRestaurant(RestaurantRequest request);

   
}