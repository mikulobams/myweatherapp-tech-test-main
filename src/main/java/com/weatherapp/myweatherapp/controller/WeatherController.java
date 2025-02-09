package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.exception.DaylightException;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class WeatherController {

    @Autowired
    WeatherService weatherService;

    @GetMapping("/forecast/{city}")
    public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {

        CityInfo ci = weatherService.forecastByCity(city);

        return ResponseEntity.ok(ci);
    }

    // TODO: given two city names, compare the length of the daylight hours and return the city with the longest day

    // TODO: given two city names, check which city its currently raining in

    /**
     * Given two city names, this method compares the length of the daylight hours and returns the city with the longest day
     * If the daylight hours are equal in both cities, an exception is thrown
     *
     * @param city1
     * @param city2
     * @return CityInfo
     */
    @GetMapping("/compare-daylight")
    public ResponseEntity<CityInfo> compareDaylight(@RequestParam("city1") String city1, @RequestParam("city2") String city2) {

        // Get the forecast for the two cities
        CityInfo cityInfo1 = weatherService.forecastByCity(city1);
        CityInfo cityInfo2 = weatherService.forecastByCity(city2);

        // Get the sunrise and sunset times for the two cities
        String sunrise1 = cityInfo1.getSunrise();
        String sunset1 = cityInfo1.getSunset();

        String sunrise2 = cityInfo2.getSunrise();
        String sunset2 = cityInfo2.getSunset();

        // Parse the sunrise and sunset times to LocalTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime sunriseTime1 = LocalTime.parse(sunrise1, formatter);
        LocalTime sunsetTime1 = LocalTime.parse(sunset1, formatter);
        LocalTime sunriseTime2 = LocalTime.parse(sunrise2, formatter);
        LocalTime sunsetTime2 = LocalTime.parse(sunset2, formatter);

        // Calculate the duration of daylight for the two cities
        long daylight1Minutes = Duration.between(sunriseTime1, sunsetTime1).toMinutes();
        long daylight2Minutes = Duration.between(sunriseTime2, sunsetTime2).toMinutes();


        // Compare the daylight hours and return the city with the longest day
        if (daylight1Minutes > daylight2Minutes) {
            return ResponseEntity.ok(cityInfo1);
        } else if (daylight1Minutes < daylight2Minutes) {
            return ResponseEntity.ok(cityInfo2);
            // Throw an exception when the daylight hours are equal in both cities
        } else {
            throw new DaylightException("Daylight hours are equal in both cities");
        }
    }

    /**
     * Given two city names, this method checks which city its currently raining in
     * If it is currently raining in both cities, it returns both cities
     * If it is currently raining in one city, it returns that city
     * If it is not currently raining in both cities, it returns an empty list
     *
     * @param city1
     * @param city2
     * @return List<CityInfo>
     */
    @GetMapping("/compare-rain")
    public ResponseEntity<List<CityInfo>> compareRain(@RequestParam("city1") String city1, @RequestParam("city2") String city2) {

        // Get the forecast for the two cities
        CityInfo cityInfo1 = weatherService.forecastByCity(city1);
        CityInfo cityInfo2 = weatherService.forecastByCity(city2);

        // Check if it is currently raining in the two cities
        Boolean isRaining1 = cityInfo1.getCurrentConditions().toLowerCase().contains("rain");
        Boolean isRaining2 = cityInfo2.getCurrentConditions().toLowerCase().contains("rain");
        System.out.println(cityInfo1.getCurrentConditions());

        // Return two cities when it is currently raining in both
        if (isRaining1 && isRaining2) {
            List<CityInfo> cities = List.of(cityInfo1, cityInfo2);
            return ResponseEntity.ok(cities);
            // Return the city where it is currently raining
        } else if (isRaining1) {
            return ResponseEntity.ok(List.of(cityInfo1));
            //  Return the city where it is currently raining
        } else if (isRaining2) {
            return ResponseEntity.ok(List.of(cityInfo2));
            // Return an empty list when it is not currently raining in both cities
        } else {
            return ResponseEntity.ok(List.of());
        }
    }


}
