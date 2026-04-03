package com.weather.models;

/**
 * Rappresenta i dati meteo restituiti dall'API, inclusi temperatura, umidità e descrizione.
 */
public class WeatherData {
    private double temperature;
    private double humidity;
    private double windSpeed;
    private double precipitation;
    private int weatherCode;
    private String description;

    public WeatherData() {
    }

    /**
     * Crea un nuovo oggetto meteo con i dati specificati.
     *
     * @param temperature temperatura in gradi Celsius
     * @param humidity umidità relativa in percentuale
     * @param windSpeed velocità del vento in km/h
     * @param precipitation precipitazioni in mm
     * @param description descrizione testuale delle condizioni meteo
     */
    public WeatherData(double temperature, double humidity, double windSpeed, double precipitation, String description) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.precipitation = precipitation;
        this.description = description;
    }

    /**
     * Crea un nuovo oggetto meteo con i dati di temperatura, umidità e descrizione.
     *
     * @param temperature temperatura in gradi Celsius
     * @param humidity umidità relativa in percentuale
     * @param description descrizione testuale delle condizioni meteo
     */
    public WeatherData(double temperature, double humidity, String description) {
        this(temperature, humidity, 0.0, 0.0, description);
    }

    /**
     * Restituisce la temperatura in gradi Celsius.
     *
     * @return temperatura meteo
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Imposta la temperatura in gradi Celsius.
     *
     * @param temperature temperatura meteo da impostare
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Restituisce l'umidità relativa in percentuale.
     *
     * @return umidità meteo
     */
    public double getHumidity() {
        return humidity;
    }

    /**
     * Imposta l'umidità relativa in percentuale.
     *
     * @param humidity umidità meteo da impostare
     */
    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    /**
     * Restituisce la velocità del vento in km/h.
     *
     * @return velocità del vento
     */
    public double getWindSpeed() {
        return windSpeed;
    }

    /**
     * Imposta la velocità del vento in km/h.
     *
     * @param windSpeed velocità del vento da impostare
     */
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * Restituisce le precipitazioni in millimetri.
     *
     * @return precipitazioni meteo
     */
    public double getPrecipitation() {
        return precipitation;
    }

    /**
     * Imposta le precipitazioni in millimetri.
     *
     * @param precipitation precipitazioni da impostare
     */
    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    /**
     * Restituisce il codice meteo Open-Meteo.
     *
     * @return codice meteo
     */
    public int getWeatherCode() {
        return weatherCode;
    }

    /**
     * Imposta il codice meteo Open-Meteo.
     *
     * @param weatherCode codice meteo da impostare
     */
    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    /**
     * Restituisce la descrizione testuale delle condizioni meteo.
     *
     * @return descrizione meteo
     */
    public String getDescription() {
        return description;
    }

    /**
     * Imposta la descrizione testuale delle condizioni meteo.
     *
     * @param description descrizione meteo da impostare
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
