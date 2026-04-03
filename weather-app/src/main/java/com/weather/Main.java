package com.weather;

import com.weather.models.WeatherData;
import com.weather.service.WeatherService;
import com.weather.service.WeatherServiceException;
import com.weather.ui.WeatherDisplay;

import java.util.Scanner;

public class Main {
    /**
     * Punto di ingresso dell'applicazione meteo. Legge il nome della città dall'input
     * dell'utente, richiama il servizio meteo e mostra i risultati sulla console.
     *
     * @param args argomenti da riga di comando (non usati)
     */
    public static void main(String[] args) {
        WeatherService weatherService = new WeatherService();
        WeatherDisplay weatherDisplay = new WeatherDisplay();

        System.out.print("Inserisci il nome della città: ");
        Scanner scanner = new Scanner(System.in);
        String city = scanner.nextLine();

        try {
            WeatherData weatherData = weatherService.getWeather(city);
            weatherDisplay.showWeather(weatherData);
        } catch (IllegalArgumentException e) {
            System.err.println("Errore: " + e.getMessage());
        } catch (WeatherServiceException e) {
            System.err.println("Errore durante il recupero del meteo: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}