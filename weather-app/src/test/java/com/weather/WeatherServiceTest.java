package com.weather;

import com.weather.api.WeatherApi;
import com.weather.models.DailyForecast;
import com.weather.models.ForecastData;
import com.weather.models.WeatherData;
import com.weather.service.WeatherService;
import com.weather.service.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceTest {

    private WeatherService weatherService;
    private WeatherApi weatherApiClient;

    @BeforeEach
    void setUp() {
        weatherApiClient = Mockito.mock(WeatherApi.class);
        weatherService = new WeatherService(weatherApiClient);
    }

    @Test
    void shouldReturnWeatherDataWhenApiClientReturnsData() {
        String city = "Rome";
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setTemperature(21.5);
        mockWeatherData.setHumidity(60);
        mockWeatherData.setWindSpeed(12.3);
        mockWeatherData.setPrecipitation(1.2);
        mockWeatherData.setDescription("Soleggiato");

        Mockito.when(weatherApiClient.fetchWeatherData(city)).thenReturn(mockWeatherData);

        WeatherData result = weatherService.getWeather(city);

        assertNotNull(result, "Il risultato non deve essere null");
        assertEquals(21.5, result.getTemperature(), 0.01);
        assertEquals(60.0, result.getHumidity(), 0.01);
        assertEquals(12.3, result.getWindSpeed(), 0.01);
        assertEquals(1.2, result.getPrecipitation(), 0.01);
        assertEquals("Soleggiato", result.getDescription());
    }

    @Test
    void shouldThrowWhenApiClientFails() {
        String city = "InvalidCity";

        Mockito.when(weatherApiClient.fetchWeatherData(city))
                .thenThrow(new RuntimeException("API Error"));

        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> weatherService.getWeather(city));

        assertTrue(exception.getMessage().contains("Impossibile ottenere il meteo per la città: InvalidCity"));
        assertNotNull(exception.getCause());
        assertEquals("API Error", exception.getCause().getMessage());
    }

    @Test
    void shouldThrowWhenCityNameIsEmpty() {
        String city = "  ";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> weatherService.getWeather(city));

        assertEquals("Il nome della città non può essere vuoto", exception.getMessage());
    }

    @Test
    void shouldCacheWeatherDataForOneHour() {
        String city = "Rome";
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setTemperature(21.5);
        mockWeatherData.setHumidity(60);
        mockWeatherData.setDescription("Soleggiato");

        Mockito.when(weatherApiClient.fetchWeatherData(city)).thenReturn(mockWeatherData);

        WeatherData firstResult = weatherService.getWeather(city);
        WeatherData secondResult = weatherService.getWeather(city.toLowerCase());

        Mockito.verify(weatherApiClient, Mockito.times(1)).fetchWeatherData(city);
        assertSame(firstResult, secondResult);
    }

    @Test
    void shouldReturnFiveDayForecast() {
        String city = "Rome";
        ForecastData forecastData = new ForecastData();
        forecastData.setCity(city);
        forecastData.setDailyForecasts(Arrays.asList(
                new DailyForecast("2026-03-29", 10.0, 18.0, "Cielo sereno"),
                new DailyForecast("2026-03-30", 11.0, 19.0, "Parzialmente nuvoloso")
        ));

        Mockito.when(weatherApiClient.fetchFiveDayForecast(city)).thenReturn(forecastData);

        ForecastData result = weatherService.getFiveDayForecast(city);

        assertNotNull(result);
        assertEquals(city, result.getCity());
        assertEquals(2, result.getDailyForecasts().size());
        assertEquals("Cielo sereno", result.getDailyForecasts().get(0).getDescription());
    }

    @Test
    void shouldCallApiClientWithSameCityName() {
        String city = "Milan";
        WeatherData mockWeatherData = new WeatherData();
        mockWeatherData.setTemperature(18.0);
        mockWeatherData.setHumidity(55);
        mockWeatherData.setDescription("Nuvoloso");

        Mockito.when(weatherApiClient.fetchWeatherData(city)).thenReturn(mockWeatherData);
        weatherService.getWeather(city);

        Mockito.verify(weatherApiClient, Mockito.times(1)).fetchWeatherData(city);
    }
}
