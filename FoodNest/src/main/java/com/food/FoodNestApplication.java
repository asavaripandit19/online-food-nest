package com.food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FoodNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodNestApplication.class, args);
		System.out.println("Ok");
	}
	
	

}
