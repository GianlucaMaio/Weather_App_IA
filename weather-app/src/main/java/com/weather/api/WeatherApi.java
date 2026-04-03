package com.weather.api;

import com.weather.models.ForecastData;
import com.weather.models.WeatherData;

/**
 * Interfaccia che definisce il contratto per i client meteo.
 * Implementazioni concrete devono recuperare i dati meteo per una città specifica.
 */
public interface WeatherApi {

    /**
     * Recupera i dati meteo per la città specificata.
     *
     * @param city il nome della città da cercare
     * @return un oggetto {@link WeatherData} contenente le informazioni meteo
     */
    WeatherData fetchWeatherData(String city);

    /**
     * Recupera la previsione meteo a 5 giorni per la città specificata.
     *
     * @param city il nome della città da cercare
     * @return un oggetto {@link ForecastData} contenente la previsione a 5 giorni
     */
    ForecastData fetchFiveDayForecast(String city);
}
