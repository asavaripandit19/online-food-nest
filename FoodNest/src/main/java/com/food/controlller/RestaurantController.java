package com.food.controlller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.food.dto.RestaurantRequest;
import com.food.exception.ApiResponse;
import com.food.service.RestaurantService;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	@PostMapping("/create")
	public ApiResponse createRestaurant(@RequestBody RestaurantRequest request) {
		return restaurantService.createRestaurant(request);
	}

	@PostMapping(value = "/logo/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse uploadLogo(@PathVariable Long id, @RequestPart("logo") MultipartFile logo) throws IOException {

		return restaurantService.uploadLogo(id, logo);
	}

	@PostMapping(value = "/cover/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse uploadCover(@PathVariable Long id, @RequestPart("cover") MultipartFile coverImage)
			throws IOException {

		return restaurantService.uploadCover(id, coverImage);
	}
}