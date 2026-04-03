package com.weather.models;

/**
 * Rappresenta una singola voce della previsione meteo giornaliera.
 */
public class DailyForecast {
    private String date;
    private double minTemperature;
    private double maxTemperature;
    private String description;

    public DailyForecast() {
    }

    public DailyForecast(String date, double minTemperature, double maxTemperature, String description) {
        this.date = date;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
