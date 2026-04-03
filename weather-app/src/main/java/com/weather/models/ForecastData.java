package com.weather.models;

import java.util.List;

/**
 * Rappresenta la previsione meteo a 5 giorni per una città.
 */
public class ForecastData {
    private String city;
    private List<DailyForecast> dailyForecasts;

    public ForecastData() {
    }

    public ForecastData(String city, List<DailyForecast> dailyForecasts) {
        this.city = city;
        this.dailyForecasts = dailyForecasts;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<DailyForecast> getDailyForecasts() {
        return dailyForecasts;
    }

    public void setDailyForecasts(List<DailyForecast> dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }
}
