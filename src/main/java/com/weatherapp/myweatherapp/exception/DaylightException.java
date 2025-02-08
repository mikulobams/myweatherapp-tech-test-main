package com.weatherapp.myweatherapp.exception;

/**
 * Exception thrown when both daylight hours are equal
 */
public class DaylightException extends RuntimeException {

    public DaylightException(String message) {
        super(message);
    }
}
