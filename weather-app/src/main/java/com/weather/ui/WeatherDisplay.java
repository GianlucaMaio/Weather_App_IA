package com.weather.ui;

import com.weather.models.ForecastData;
import com.weather.models.WeatherData;

public class WeatherDisplay
 {

    /**
     * Visualizza i dati meteo formattati sulla console.
     *
     * @param data i dati meteo da mostrare all'utente
     */
    public void showWeather(WeatherData data) {
        System.out.println("Informazioni meteo:");
        System.out.println("Temperatura: " + data.getTemperature() + "°C");
        System.out.println("Umidità: " + data.getHumidity() + "%");
        System.out.println("Vento: " + data.getWindSpeed() + " km/h");
        System.out.println("Pioggia: " + data.getPrecipitation() + " mm");
        System.out.println("Descrizione: " + data.getDescription());
    }

    /**
     * Mostra la previsione meteo a 5 giorni in formato semplice.
     *
     * @param forecastData i dati della previsione a 5 giorni
     */
    public void showFiveDayForecast(ForecastData forecastData) {
        System.out.println("Previsione meteo a 5 giorni per: " + forecastData.getCity());
        System.out.println("----------------------------------------");
        forecastData.getDailyForecasts().forEach(day -> {
            System.out.println(day.getDate() + ": "
                    + "min " + day.getMinTemperature() + "°C, "
                    + "max " + day.getMaxTemperature() + "°C, "
                    + day.getDescription());
        });
    }
}
