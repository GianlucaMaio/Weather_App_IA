package com.weather.service;

/**
 * Eccezione del livello servizio usata quando il recupero dei dati meteo fallisce.
 * Questa classe incapsula errori di chiamata all'API o problemi di elaborazione.
 */
public class WeatherServiceException extends RuntimeException {

    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
