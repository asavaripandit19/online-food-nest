package com.food.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. EMAIL ALREADY EXISTS (IMPORTANT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExists(
            EmailAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }

    @ExceptionHandler(MobileAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleMobileExists(
            MobileAlreadyExistsException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(false, ex.getMessage(), null));
    }

    // ============================
    // CHANGED: IMPROVED VALIDATION (single unified format)
    // ============================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(
                        false,
                        "Validation failed",
                        errors.toString()   // CHANGED PART (added structured errors)
                ));
    }

    // ============================
    // ENUM ERROR HANDLING (UNCHANGED BUT CLEAN)
    // ============================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleEnumError(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getMostSpecificCause();

        String msg = (cause != null && cause.getMessage() != null)
                ? cause.getMessage()
                : ex.getMessage();

        if (msg.contains("ServiceType")) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(
                            false,
                            "Invalid ServiceType. Allowed: BREAKFAST, LUNCH, DINNER, REGULAR_FOOD_ORDERS",
                            null
                    ));
        }

        if (msg.contains("FoodType")) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(
                            false,
                            "Invalid FoodType. Allowed: VEG, NON_VEG, BOTH",
                            null
                    ));
        }

        return ResponseEntity
                .badRequest()
                .body(new ApiResponse(
                        false,
                        "Invalid request format or enum value",
                        null
                ));
    }

    // ============================
    // BUSINESS TYPE ERROR
    // ============================
    @ExceptionHandler(InvalidBusinessTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBusinessTypeEntity(
            InvalidBusinessTypeException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", ex.getMessage());
        error.put("errorType", "CLOUD_KITCHEN, DINE_IN, TAKEAWAY");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ============================
    // RESTAURANT NOT FOUND
    // ============================
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ApiResponse> handleRestaurantNotFound(
            RestaurantNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        false,
                        ex.getMessage(),
                        null
                ));
    }

    // ============================
    // CHANGED: SINGLE CLEAN VALIDATION HANDLER REMOVED DUPLICATE LOGIC
    // ============================


    // ============================
    // CHANGED: GENERIC EXCEPTION (SAFE MESSAGE)
    // ============================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {

        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(
                        false,
                        "Something went wrong. Please try again later.",
                        null   // CHANGED: removed ex.getMessage() for safety
                ));
    }
}