package com.weatherapp.myweatherapp;

import com.weatherapp.myweatherapp.controller.WeatherController;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Constructor;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    /**
     * Utility method to create an instance of CityInfo.CurrentConditions
     * using reflection.
     */
    private Object createCurrentConditionsInstance() throws Exception {
        // Fully qualified name of the inner class. Note the $ separating the outer and inner class names.
        String innerClassName = "com.weatherapp.myweatherapp.model.CityInfo$CurrentConditions";
        Class<?> ccClass = Class.forName(innerClassName);
        Constructor<?> ctor = ccClass.getDeclaredConstructor();
        ctor.setAccessible(true); // in case the constructor is not public
        return ctor.newInstance();
    }


    /**
     * Test the /compare-daylight/{city1}/{city2} endpoint when CityTwo has a longer day.
     */
    @Test
    public void testCompareDaylight_CityTwoHasLongerDay() throws Exception {
        // Build CityOne: 06:00 to 18:00 (720 minutes)
        CityInfo cityOne = new CityInfo();
        ReflectionTestUtils.setField(cityOne, "address", "CityOne");
        Object conditionsOne = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsOne, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsOne, "sunset", "18:00:00");
        ReflectionTestUtils.setField(cityOne, "currentConditions", conditionsOne);

        // Build CityTwo: 06:00 to 19:00 (780 minutes)
        CityInfo cityTwo = new CityInfo();
        ReflectionTestUtils.setField(cityTwo, "address", "CityTwo");
        Object conditionsTwo = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsTwo, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "sunset", "19:00:00");
        ReflectionTestUtils.setField(cityTwo, "currentConditions", conditionsTwo);

        when(weatherService.forecastByCity("CityOne")).thenReturn(cityOne);
        when(weatherService.forecastByCity("CityTwo")).thenReturn(cityTwo);

        // Expect CityTwo to be returned (since its daylight duration is longer)
        mockMvc.perform(get("/compare-daylight/CityOne/CityTwo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("CityTwo"));
    }

    /**
     * Test the /compare-daylight/{city1}/{city2} endpoint when daylight durations are equal.
     * According to your controller, it returns ResponseEntity.ok(null) when equal.
     */
    @Test
    public void testCompareDaylight_EqualDaylight() throws Exception {
        // Build Both cities: 06:00 to 18:00.
        CityInfo cityOne = new CityInfo();
        ReflectionTestUtils.setField(cityOne, "address", "CityOne");
        Object conditionsOne = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsOne, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsOne, "sunset", "18:00:00");
        ReflectionTestUtils.setField(cityOne, "currentConditions", conditionsOne);

        CityInfo cityTwo = new CityInfo();
        ReflectionTestUtils.setField(cityTwo, "address", "CityTwo");
        Object conditionsTwo = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsTwo, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "sunset", "18:00:00");
        ReflectionTestUtils.setField(cityTwo, "currentConditions", conditionsTwo);

        when(weatherService.forecastByCity("CityOne")).thenReturn(cityOne);
        when(weatherService.forecastByCity("CityTwo")).thenReturn(cityTwo);

        // Expect a response body of "null" when daylight durations are equal.
        mockMvc.perform(get("/compare-daylight/CityOne/CityTwo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Daylight hours are equal in both cities"));
    }

    /**
     * Test the /compare-rain/{city1}/{city2} endpoint when both cities are raining.
     */
    @Test
    public void testCompareRain_BothRaining() throws Exception {
        // Build CityOne: conditions contain "rain"
        CityInfo cityOne = new CityInfo();
        ReflectionTestUtils.setField(cityOne, "address", "CityOne");
        Object conditionsOne = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsOne, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsOne, "sunset", "18:00:00");
        ReflectionTestUtils.setField(conditionsOne, "conditions", "heavy rain");
        ReflectionTestUtils.setField(cityOne, "currentConditions", conditionsOne);

        // Build CityTwo: conditions contain "rain"
        CityInfo cityTwo = new CityInfo();
        ReflectionTestUtils.setField(cityTwo, "address", "CityTwo");
        Object conditionsTwo = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsTwo, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "sunset", "18:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "conditions", "light rain");
        ReflectionTestUtils.setField(cityTwo, "currentConditions", conditionsTwo);

        when(weatherService.forecastByCity("CityOne")).thenReturn(cityOne);
        when(weatherService.forecastByCity("CityTwo")).thenReturn(cityTwo);

        // Expect both cities in the returned JSON array.
        mockMvc.perform(get("/compare-rain/CityOne/CityTwo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].address").value("CityOne"))
                .andExpect(jsonPath("$[1].address").value("CityTwo"));
    }

    /**
     * Test the /compare-rain/{city1}/{city2} endpoint when only one city is raining.
     */
    @Test
    public void testCompareRain_OneRaining() throws Exception {
        // Build CityOne: clear conditions.
        CityInfo cityOne = new CityInfo();
        ReflectionTestUtils.setField(cityOne, "address", "CityOne");
        Object conditionsOne = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsOne, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsOne, "sunset", "18:00:00");
        ReflectionTestUtils.setField(conditionsOne, "conditions", "clear sky");
        ReflectionTestUtils.setField(cityOne, "currentConditions", conditionsOne);

        // Build CityTwo: raining.
        CityInfo cityTwo = new CityInfo();
        ReflectionTestUtils.setField(cityTwo, "address", "CityTwo");
        Object conditionsTwo = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsTwo, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "sunset", "18:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "conditions", "drizzle rain");
        ReflectionTestUtils.setField(cityTwo, "currentConditions", conditionsTwo);

        when(weatherService.forecastByCity("CityOne")).thenReturn(cityOne);
        when(weatherService.forecastByCity("CityTwo")).thenReturn(cityTwo);

        // Expect only CityTwo to be returned.
        mockMvc.perform(get("/compare-rain/CityOne/CityTwo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].address").value("CityTwo"));
    }

    /**
     * Test the /compare-rain/{city1}/{city2} endpoint when neither city is raining.
     */
    @Test
    public void testCompareRain_NoneRaining() throws Exception {
        // Build CityOne: sunny.
        CityInfo cityOne = new CityInfo();
        ReflectionTestUtils.setField(cityOne, "address", "CityOne");
        Object conditionsOne = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsOne, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsOne, "sunset", "18:00:00");
        ReflectionTestUtils.setField(conditionsOne, "conditions", "sunny");
        ReflectionTestUtils.setField(cityOne, "currentConditions", conditionsOne);

        // Build CityTwo: clear.
        CityInfo cityTwo = new CityInfo();
        ReflectionTestUtils.setField(cityTwo, "address", "CityTwo");
        Object conditionsTwo = createCurrentConditionsInstance();
        ReflectionTestUtils.setField(conditionsTwo, "sunrise", "06:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "sunset", "18:00:00");
        ReflectionTestUtils.setField(conditionsTwo, "conditions", "clear");
        ReflectionTestUtils.setField(cityTwo, "currentConditions", conditionsTwo);

        when(weatherService.forecastByCity("CityOne")).thenReturn(cityOne);
        when(weatherService.forecastByCity("CityTwo")).thenReturn(cityTwo);

        // Expect an empty JSON array.
        mockMvc.perform(get("/compare-rain/CityOne/CityTwo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
