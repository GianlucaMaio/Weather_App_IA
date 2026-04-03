/**
 * Recupera il meteo attuale per una città usando le API Open-Meteo.
 *
 * @param {string} city - Il nome della città da cercare.
 * @returns {Promise<Object>} - Un oggetto JSON con città, temperatura e descrizione.
 */
async function getCurrentWeatherByCity(city) {
  // Validazione dell'input: evita stringhe vuote, valori null/undefined e spazi bianchi.
  if (!city || typeof city !== 'string' || city.trim() === '') {
    throw new Error('Inserisci un nome di città valido.');
  }

  const cityName = city.trim();

  try {
    // Costruisce l'URL di geocoding con encodeURIComponent per gestire spazi e caratteri speciali.
    const geocodingUrl = `https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(cityName)}&count=1`;

    // Chiamata fetch per ottenere latitudine e longitudine.
    const geoResponse = await fetch(geocodingUrl);

    // Controllo dello stato HTTP della risposta.
    if (!geoResponse.ok) {
      throw new Error(`Errore API geocoding: ${geoResponse.status}`);
    }

    // Parsing JSON della risposta di geocoding.
    const geoData = await geoResponse.json();

    // Validazione del JSON: controlla che ci siano risultati.
    const location = geoData.results?.[0];
    if (!location || typeof location.latitude !== 'number' || typeof location.longitude !== 'number') {
      throw new Error(`Città non trovata o risposta API non valida per: ${cityName}`);
    }

    const { latitude, longitude, name } = location;

    // Costruisce l'URL per il meteo corrente.
    const weatherUrl = `https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&current_weather=true&hourly=relativehumidity_2m,windspeed_10m,precipitation&daily=temperature_2m_max,temperature_2m_min,weathercode&forecast_days=5&timezone=auto`;

    // Chiamata fetch per ottenere il meteo attuale e la previsione a 5 giorni.
    const weatherResponse = await fetch(weatherUrl);
    if (!weatherResponse.ok) {
      throw new Error(`Errore API meteo: ${weatherResponse.status}`);
    }

    // Parsing JSON della risposta meteo.
    const weatherData = await weatherResponse.json();

    // Controllo che il campo current_weather esista.
    const currentWeather = weatherData.current_weather;
    const daily = weatherData.daily;
    const hourly = weatherData.hourly;
    if (!currentWeather || typeof currentWeather.temperature !== 'number') {
      throw new Error(`Risposta meteo incompleta per la città: ${name}`);
    }
    if (!daily || !Array.isArray(daily.time) || daily.time.length === 0) {
      throw new Error(`Previsione a 5 giorni non disponibile per: ${name}`);
    }
    if (!hourly || !Array.isArray(hourly.time) || hourly.time.length === 0) {
      throw new Error(`Dati orari non disponibili per: ${name}`);
    }

    const description = getWeatherDescription(currentWeather.weathercode);
    const icon = getWeatherIcon(currentWeather.weathercode);
    const humidity = parseHourlyValue(hourly, currentWeather.time, 'relativehumidity_2m');
    const windSpeed = parseHourlyValue(hourly, currentWeather.time, 'windspeed_10m');
    const precipitation = parseHourlyValue(hourly, currentWeather.time, 'precipitation');
    const forecast = buildFiveDayForecast(daily);

    return {
      city: name,
      temperature: currentWeather.temperature,
      description: description,
      icon: icon,
      humidity: humidity,
      windSpeed: windSpeed,
      precipitation: precipitation,
      forecast: forecast
    };
  } catch (error) {
    // Gestisce errori di rete o richieste non valide.
    if (error instanceof TypeError) {
      throw new Error('Problema di rete o richiesta non valida.');
    }
    throw error;
  }
}

/**
 * Converte il codice meteo Open-Meteo in una descrizione semplice.
 *
 * @param {number} code - Codice meteo restituito dall'API.
 * @returns {string} - Descrizione del tempo.
 */
function getWeatherDescription(code) {
  switch (code) {
    case 0:
      return 'Cielo sereno';
    case 1:
    case 2:
    case 3:
      return 'Parzialmente nuvoloso';
    case 45:
    case 48:
      return 'Nebbia';
    case 51:
    case 53:
    case 55:
      return 'Pioggia leggera';
    case 61:
    case 63:
    case 65:
      return 'Pioggia';
    case 71:
    case 73:
    case 75:
      return 'Neve';
    case 95:
    case 96:
    case 99:
      return 'Temporale';
    default:
      return 'Condizioni variabili';
  }
}

function getWeatherIcon(code) {
  switch (code) {
    case 0:
      return { src: 'icons/sunny.svg', alt: 'Cielo sereno' };
    case 1:
    case 2:
    case 3:
      return { src: 'icons/partly-cloudy.svg', alt: 'Parzialmente nuvoloso' };
    case 45:
    case 48:
      return { src: 'icons/fog.svg', alt: 'Nebbia' };
    case 51:
    case 53:
    case 55:
      return { src: 'icons/rain.svg', alt: 'Pioggia leggera' };
    case 61:
    case 63:
    case 65:
      return { src: 'icons/rain.svg', alt: 'Pioggia' };
    case 71:
    case 73:
    case 75:
      return { src: 'icons/snow.svg', alt: 'Neve' };
    case 95:
    case 96:
    case 99:
      return { src: 'icons/thunderstorm.svg', alt: 'Temporale' };
    default:
      return { src: 'icons/cloudy.svg', alt: 'Condizioni variabili' };
  }
}

function parseHourlyValue(hourly, time, key) {
  const times = hourly.time;
  const values = hourly[key];
  if (!Array.isArray(times) || !Array.isArray(values)) {
    return null;
  }

  let index = times.indexOf(time);
  if (index === -1) {
    index = times.findIndex(t => t === time || t === time.replace('Z', ''));
  }

  if (index === -1) {
    const normalizedTime = time.substring(0, 13);
    index = times.findIndex(t => t.substring(0, 13) === normalizedTime);
  }

  if (index === -1) {
    const currentDate = new Date(time);
    if (!Number.isNaN(currentDate.getTime())) {
      index = times.findIndex(t => {
        const date = new Date(t);
        return !Number.isNaN(date.getTime()) && date.getTime() === currentDate.getTime();
      });
    }
  }

  if (index === -1) {
    return null;
  }

  return values[index];
}

function buildFiveDayForecast(daily) {
  const dates = daily.time;
  const minTemps = daily.temperature_2m_min;
  const maxTemps = daily.temperature_2m_max;
  const weatherCodes = daily.weathercode;

  return dates.map((date, index) => ({
    date: date,
    minTemperature: minTemps[index],
    maxTemperature: maxTemps[index],
    description: getWeatherDescription(weatherCodes[index])
  }));
}

// Esempio di chiamata:
// getCurrentWeatherByCity('Rome')
//   .then(data => console.log(data))
//   .catch(error => console.error(error.message));
