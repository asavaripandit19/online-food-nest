package com.food.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. EMAIL ALREADY EXISTS (IMPORTANT)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailExists(EmailAlreadyExistsException ex) {

        return new ResponseEntity<>(
                new ApiResponse(false, ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    // 2. ALL OTHER EXCEPTIONS (FALLBACK)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {

        return new ResponseEntity<>(
                new ApiResponse(false, ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    
    @ExceptionHandler(MobileAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleMobileExists(MobileAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ApiResponse(false, ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }
}