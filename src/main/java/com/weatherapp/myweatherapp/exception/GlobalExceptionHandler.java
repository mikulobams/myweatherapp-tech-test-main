package com.weatherapp.myweatherapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler class is used to handle the exceptions thrown by the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DaylightException.class)
    public ResponseEntity<String> handleEqualDaylightException(DaylightException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
