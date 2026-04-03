package com.weather.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weather.models.DailyForecast;
import com.weather.models.ForecastData;
import com.weather.models.WeatherData;

import java.io.BufferedReader;
import java.util.List;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Client per l'API Open-Meteo che gestisce la geocodifica della città e la richiesta delle previsioni.
 */
public class WeatherApiClient implements WeatherApi {

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";

    /**
     * Recupera i dati meteo correnti per la città fornita.
     *
     * @param city il nome della città da cercare
     * @return un oggetto {@link com.weather.models.WeatherData} contenente temperatura, umidità e descrizione
     * @throws RuntimeException se la geocodifica fallisce, se la città non viene trovata o se l'API non risponde correttamente
     */
    public WeatherData fetchWeatherData(String city) {
        Coordinates coordinates = geocodeCity(city);
        String forecastUrl = FORECAST_URL
                + "?latitude=" + coordinates.latitude
                + "&longitude=" + coordinates.longitude
                + "&current_weather=true"
                + "&hourly=relativehumidity_2m,windspeed_10m,precipitation"
                + "&timezone=auto";

        String forecastJson = getUrlContent(forecastUrl);
        JsonObject forecastObject = JsonParser.parseString(forecastJson).getAsJsonObject();
        JsonObject currentWeather = forecastObject.getAsJsonObject("current_weather");

        if (currentWeather == null) {
            throw new RuntimeException("Impossibile ottenere il meteo per la città: " + city);
        }

        double temperature = currentWeather.get("temperature").getAsDouble();
        int weatherCode = currentWeather.get("weathercode").getAsInt();
        String time = currentWeather.get("time").getAsString();

        double humidity = parseHourlyValue(forecastObject, time, "relativehumidity_2m");
        double windSpeed = parseHourlyValue(forecastObject, time, "windspeed_10m");
        double precipitation = parseHourlyValue(forecastObject, time, "precipitation");
        String description = weatherCodeToDescription(weatherCode);

        WeatherData weatherData = new WeatherData(temperature, humidity, windSpeed, precipitation, description);
        weatherData.setWeatherCode(weatherCode);
        return weatherData;
    }

    /**
     * Recupera la previsione meteo a 5 giorni per la città fornita.
     *
     * @param city il nome della città da cercare
     * @return un oggetto {@link ForecastData} con la previsione a 5 giorni
     */
    public ForecastData fetchFiveDayForecast(String city) {
        Coordinates coordinates = geocodeCity(city);
        String forecastUrl = FORECAST_URL
                + "?latitude=" + coordinates.latitude
                + "&longitude=" + coordinates.longitude
                + "&daily=temperature_2m_max,temperature_2m_min,weathercode"
                + "&forecast_days=5"
                + "&timezone=auto";

        String forecastJson = getUrlContent(forecastUrl);
        JsonObject forecastObject = JsonParser.parseString(forecastJson).getAsJsonObject();
        JsonObject daily = forecastObject.getAsJsonObject("daily");

        if (daily == null) {
            throw new RuntimeException("Impossibile ottenere la previsione meteo per la città: " + city);
        }

        JsonArray dates = daily.getAsJsonArray("time");
        JsonArray minTemperatures = daily.getAsJsonArray("temperature_2m_min");
        JsonArray maxTemperatures = daily.getAsJsonArray("temperature_2m_max");
        JsonArray weatherCodes = daily.getAsJsonArray("weathercode");

        if (dates == null || minTemperatures == null || maxTemperatures == null || weatherCodes == null) {
            throw new RuntimeException("Dati previsionali incompleti per la città: " + city);
        }

        int days = Math.min(dates.size(), Math.min(minTemperatures.size(), Math.min(maxTemperatures.size(), weatherCodes.size())));
        List<DailyForecast> forecasts = new java.util.ArrayList<>();

        for (int i = 0; i < days; i++) {
            String date = dates.get(i).getAsString();
            double minTemp = minTemperatures.get(i).getAsDouble();
            double maxTemp = maxTemperatures.get(i).getAsDouble();
            int weatherCode = weatherCodes.get(i).getAsInt();
            String description = weatherCodeToDescription(weatherCode);

            forecasts.add(new DailyForecast(date, minTemp, maxTemp, description));
        }

        return new ForecastData(city.trim(), forecasts);
    }

    /**
     * Richiede le coordinate geografiche della città all'API di geocodifica.
     *
     * @param city il nome della città da convertire in coordinate
     * @return un oggetto {@link Coordinates} con latitudine e longitudine
     * @throws RuntimeException se la città non viene trovata o la chiamata API fallisce
     */
    private Coordinates geocodeCity(String city) {
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        String geoUrl = GEOCODING_URL + "?name=" + encodedCity + "&count=1";
        String geoJson = getUrlContent(geoUrl);

        JsonObject geoObject = JsonParser.parseString(geoJson).getAsJsonObject();
        JsonArray results = geoObject.has("results") ? geoObject.getAsJsonArray("results") : null;

        if (results == null || results.size() == 0) {
            throw new RuntimeException("Città non trovata: " + city);
        }

        JsonObject location = results.get(0).getAsJsonObject();
        double latitude = location.get("latitude").getAsDouble();
        double longitude = location.get("longitude").getAsDouble();

        return new Coordinates(latitude, longitude);
    }

    /**
     * Estrae il valore di umidità corrispondente all'ora corrente dai dati orari ricevuti dall'API.
     *
     * @param forecastObject il payload JSON della previsione Open-Meteo
     * @param time il timestamp dell'ora corrente restituito dall'oggetto current_weather
     * @return il valore di umidità corrispondente, oppure 0.0 se il dato non è disponibile
     */
    private double parseHourlyValue(JsonObject forecastObject, String time, String key) {
        if (!forecastObject.has("hourly")) {
            return 0.0;
        }

        JsonObject hourly = forecastObject.getAsJsonObject("hourly");
        JsonArray times = hourly.getAsJsonArray("time");
        JsonArray values = hourly.getAsJsonArray(key);

        if (times == null || values == null || times.size() != values.size()) {
            return 0.0;
        }

        for (int i = 0; i < times.size(); i++) {
            if (times.get(i).getAsString().equals(time)) {
                return values.get(i).getAsDouble();
            }
        }

        return 0.0;
    }

    /**
     * Esegue una semplice richiesta HTTP GET verso l'URL specificato e restituisce il corpo della risposta.
     *
     * @param urlString l'URL da chiamare
     * @return il contenuto JSON restituito dall'API
     * @throws RuntimeException se si verifica un errore di I/O durante la chiamata HTTP
     */
    private String getUrlContent(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int statusCode = conn.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Risposta API non valida: HTTP " + statusCode);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la chiamata API: " + e.getMessage(), e);
        }
        return result.toString();
    }

    private String weatherCodeToDescription(int code) {
        switch (code) {
            case 0:
                return "Cielo sereno";
            case 1:
            case 2:
            case 3:
                return "Parzialmente nuvoloso";
            case 45:
            case 48:
                return "Nebbia";
            case 51:
            case 53:
            case 55:
                return "Pioggia leggera";
            case 61:
            case 63:
            case 65:
                return "Pioggia";
            case 71:
            case 73:
            case 75:
                return "Neve";
            case 95:
            case 96:
            case 99:
                return "Temporale";
            default:
                return "Condizioni variabili";
        }
    }

    private static class Coordinates {
        private final double latitude;
        private final double longitude;

        private Coordinates(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}