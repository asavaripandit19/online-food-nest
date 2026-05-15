package com.food.ServiceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.food.dto.RestaurantRequest;
import com.food.enums.BusinessType;
import com.food.exception.ApiResponse;
import com.food.exception.InvalidBusinessTypeException;
import com.food.exception.RestaurantNotFoundException;
import com.food.model.Restaurant;
import com.food.repository.RestaurantRepository;
import com.food.service.RestaurantService;
import com.food.service.S3Service;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    @Transactional
    public ApiResponse createRestaurant(RestaurantRequest request) {

        BusinessType type;

        try {
            type = BusinessType.valueOf(request.getBusinessType().toUpperCase());
        } catch (Exception e) {
            throw new InvalidBusinessTypeException(
                    "Invalid business type. Allowed: CLOUD_KITCHEN, DINE_IN, TAKEAWAY"
            );
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(request.getRestaurantName());
        restaurant.setOwnerName(request.getOwnerName());
        restaurant.setMobile(request.getMobile());
        restaurant.setEmail(request.getEmail());
        restaurant.setFoodType(request.getFoodType());
        restaurant.setCuisines(request.getCuisines());
        restaurant.setServiceTypes(request.getServiceTypes());
        restaurant.setBusinessType(type.name());
        restaurant.setOpenTime(LocalTime.parse(request.getOpenTime().trim()));
        restaurant.setCloseTime(LocalTime.parse(request.getCloseTime().trim()));
        restaurant.setCreatedAt(LocalDateTime.now());

        Restaurant saved = restaurantRepository.save(restaurant);

        String code = generateCode(saved.getRestaurantName(), saved.getId());
        saved.setRestaurantCode(code);

        saved = restaurantRepository.save(saved);

        return new ApiResponse(true, "Restaurant created successfully", saved.getId());
    }

    private String generateCode(String name, Long id) {
        String safeName = (name == null || name.isBlank()) ? "X" : name;
        return safeName.substring(0, 1).toUpperCase() + String.format("%02d", id);
    }

    @Override
    public ApiResponse uploadCover(Long id, MultipartFile coverImage) throws IOException {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RestaurantNotFoundException("Restaurant not found with id: " + id));

        validateImage(coverImage);

        String url = s3Service.uploadFile(coverImage);
        restaurant.setCoverImageUrl(url);

        restaurantRepository.save(restaurant);

        return new ApiResponse(true, "Cover image uploaded successfully", url);
    }

    @Override
    public ApiResponse uploadLogo(Long id, MultipartFile logo) throws IOException {

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() ->
                        new RestaurantNotFoundException("Restaurant not found with id: " + id));

        validateImage(logo);

        String url = s3Service.uploadFile(logo);
        restaurant.setLogoUrl(url);

        restaurantRepository.save(restaurant);

        return new ApiResponse(true, "Logo uploaded successfully", url);
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }
}