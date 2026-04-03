package com.weather.service;

import com.weather.api.WeatherApi;
import com.weather.api.WeatherApiClient;
import com.weather.models.ForecastData;
import com.weather.models.WeatherData;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WeatherService {
    private static final long CACHE_DURATION_MS = 60 * 60 * 1000; // 1 ora

    private final WeatherApi weatherApiClient;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public WeatherService() {
        this.weatherApiClient = new WeatherApiClient();
    }

    public WeatherService(WeatherApi weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    /**
     * Recupera i dati meteo correnti per la città specificata.
     *
     * @param city il nome della città da cercare
     * @return i dati meteo trovati
     * @throws IllegalArgumentException se il nome della città è vuoto o nullo
     * @throws WeatherServiceException se si verifica un errore durante il recupero dei dati
     */
    /**
     * Recupera i dati meteo correnti per la città specificata.
     * I risultati vengono memorizzati in cache per un'ora e riutilizzati quando disponibili.
     *
     * @param city il nome della città da cercare
     * @return i dati meteo trovati
     * @throws IllegalArgumentException se il nome della città è vuoto o nullo
     * @throws WeatherServiceException se si verifica un errore durante il recupero dei dati
     */
    public WeatherData getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della città non può essere vuoto");
        }

        String normalizedCity = normalizeCity(city);
        WeatherData cachedData = getCachedWeather(normalizedCity);
        if (cachedData != null) {
            return cachedData;
        }

        try {
            WeatherData weatherData = weatherApiClient.fetchWeatherData(city.trim());
            cacheWeather(normalizedCity, weatherData);
            return weatherData;
        } catch (RuntimeException e) {
            throw new WeatherServiceException("Impossibile ottenere il meteo per la città: " + city.trim(), e);
        }
    }

    /**
     * Recupera la previsione meteo a 5 giorni per la città specificata.
     *
     * @param city il nome della città da cercare
     * @return i dati della previsione a 5 giorni
     * @throws IllegalArgumentException se il nome della città è nullo o vuoto
     * @throws WeatherServiceException se si verifica un errore durante il recupero della previsione
     */
    public ForecastData getFiveDayForecast(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della città non può essere vuoto");
        }

        try {
            return weatherApiClient.fetchFiveDayForecast(city.trim());
        } catch (RuntimeException e) {
            throw new WeatherServiceException("Impossibile ottenere la previsione a 5 giorni per la città: " + city.trim(), e);
        }
    }

    private WeatherData getCachedWeather(String cityKey) {
        CacheEntry entry = cache.get(cityKey);
        if (entry == null) {
            return null;
        }

        if (System.currentTimeMillis() - entry.timestamp > CACHE_DURATION_MS) {
            cache.remove(cityKey);
            return null;
        }

        return entry.weatherData;
    }

    private void cacheWeather(String cityKey, WeatherData weatherData) {
        cache.put(cityKey, new CacheEntry(weatherData, System.currentTimeMillis()));
    }

    private String normalizeCity(String city) {
        return city.trim().toLowerCase(Locale.ROOT);
    }

    private static class CacheEntry {
        private final WeatherData weatherData;
        private final long timestamp;

        private CacheEntry(WeatherData weatherData, long timestamp) {
            this.weatherData = weatherData;
            this.timestamp = timestamp;
        }
    }
}
