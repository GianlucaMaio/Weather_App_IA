# Weather App

## Panoramica
Questo progetto è una semplice applicazione meteo sviluppata in Java che recupera e visualizza le condizioni meteorologiche attuali e le previsioni a 5 giorni per una città specificata. L'applicazione interagisce con le API di Open-Meteo per ottenere i dati e presenta le informazioni tramite una **interfaccia web intuitiva**.

Il progetto è stato sviluppato con il supporto dell'Intelligenza Artificiale, utilizzata non per le basi della programmazione ma per **accelerare l'ottimizzazione del codice, velocizzare il debug e aderire alle migliori pratiche di sviluppo**.

## Caratteristiche Principali
*   **Meteo Attuale:** Visualizza temperatura, umidità, velocità del vento, precipitazioni e una descrizione testuale del tempo per la città richiesta.
*   **Previsioni a 5 Giorni:** Fornisce le previsioni giornaliere con temperature minime e massime.
*   **Interfaccia Web:** Una semplice pagina HTML/CSS/JavaScript per l'input della città e la visualizzazione dei risultati direttamente nel browser.
*   **Caching Dati:** Implementazione di una cache in memoria per le query meteo, migliorando le prestazioni e riducendo le chiamate API ripetute.
*   **Gestione degli Errori:** Implementazione robusta della gestione degli errori per città non trovate o risposte API non valide.

## Struttura del Progetto
Il progetto è organizzato come un'applicazione Maven e segue una struttura standard:

```
weather-app
├── pom.xml                                 # File di configurazione Maven
├── README.md                               # Questo file
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── weather
    │   │           ├── api                 # Interfacce e client per le API meteo (Open-Meteo)
    │   │           │   ├── WeatherApi.java
    │   │           │   └── WeatherApiClient.java
    │   │           ├── models              # Classi per la modellazione dei dati (WeatherData, ForecastData, DailyForecast)
    │   │           │   ├── DailyForecast.java
    │   │           │   ├── ForecastData.java
    │   │           │   └── WeatherData.java
    │   │           ├── service             # Logica di business per il recupero e la gestione dei dati meteo (WeatherService)
    │   │           │   ├── WeatherService.java
    │   │           │   └── WeatherServiceException.java
    │   │           ├── ui                  # Classi per la visualizzazione dei dati in console (WeatherDisplay - mantenuta per modularità)
    │   │           │   └── WeatherDisplay.java
    │   │           └── Main.java           # Punto di ingresso dell'applicazione (include l'avvio del server web)
    │   └── resources
    │       ├── application.properties      # File di configurazione dell'applicazione
    │       └── static                      # Contenuti web statici (HTML, CSS, JavaScript, icone)
    │           ├── icons
    │           │   └── ... (file SVG delle icone)
    │           ├── index.html              # La pagina web principale dell'applicazione
    │           ├── styles.css              # Stili CSS per l'interfaccia web
    │           └── weather-api.js          # Logica JavaScript per l'interazione con il backend
    └── test
        └── java
            └── com
                └── weather
                    └── WeatherServiceTest.java # Test unitari per WeatherService
```

## Requisiti
*   Java Development Kit (JDK) 8 o superiore
*   Apache Maven
*   Connessione Internet per chiamare l'API Open-Meteo

## Installazione
1.  **Clona il Repository (se applicabile):**
    ```bash
    git clone <repository-url>
    cd weather-app
    ```
    *(Sostituisci `<repository-url>` con l'effettivo URL del tuo repository, se ne hai uno. Altrimenti, salta questo passaggio se hai già i file localmente.)*

2.  **Compila il Progetto:**
    Naviga nella directory radice del progetto (dove si trova il `pom.xml`) e compila l'applicazione usando Maven:
    ```bash
    mvn clean install
    ```
    Questo comando compilerà il codice sorgente, eseguirà i test e creerà il file JAR eseguibile nella cartella `target/`.

3.  **(Opzionale) Verifica i test:**
    ```bash
    mvn test
    ```

## Come Eseguire il Codice
1.  **Avvia l'Applicazione (Server Web):**
    Dopo una compilazione riuscita, puoi eseguire l'applicazione dal terminale. L'applicazione lancerà un semplice server web che ospiterà l'interfaccia utente.

    ```bash
    java -jar target/weather-app-1.0-SNAPSHOT.jar
    ```

2.  **Accedi all'Interfaccia Web:**
    Una volta avviata l'applicazione, apri il index.html nel seguente percoros src/main/resources/static/index.html

    Qui potrai interagire con l'applicazione, inserendo il nome di una città e visualizzando i risultati aggiornati dinamicamente.

## Note sull'implementazione
*   `WeatherApiClient` utilizza l'endpoint di geocoding di Open-Meteo per convertire il nome della città in coordinate geografiche.
*   Richiede quindi l'endpoint di forecast Open-Meteo per ottenere il meteo corrente e i dati di umidità, velocità del vento e precipitazioni, oltre alle previsioni a 5 giorni.
*   `WeatherService` espone un livello di astrazione che consente di mockare il provider API durante i test e implementa un meccanismo di caching.
*   L'interfaccia utente è gestita tramite `index.html`, `styles.css` e `weather-api.js`, che comunica con il backend Java per ottenere e visualizzare i dati.

## Qualità del Codice e Contributo dell'IA
Il progetto adotta nomi chiari e significativi per classi, metodi e variabili, facilitando la leggibilità e il mantenimento. L'organizzazione modulare, con una chiara separazione dei livelli (API, Models, Service, UI), rende il flusso di esecuzione logico e scalabile.

L'Intelligenza Artificiale è stata fondamentale per:
*   **Raffinamento dell'architettura:** Suggerimenti per una struttura di classi più efficiente e pattern di design.
*   **Accelerazione del Debug:** Rapida identificazione e risoluzione di bug e problemi di integrazione API.
*   **Adesione alle Best Practice:** Guida nell'implementazione di meccanismi di caching e nella comprensione delle migliori pratiche di sicurezza per la gestione dei dati e delle API.
*   **Generazione di Dettagli (come questo README):** Assistenza nella creazione di documentazione chiara e completa.

La gestione degli errori è robusta per scenari comuni, e il codice è progettato per essere facilmente comprensibile e modificabile da altri sviluppatori.

## Miglioramenti Futuri
*   **Interfaccia Utente Interattiva:** Implementare funzionalità avanzate nell'interfaccia web, come una ricerca automatica della città con suggerimenti e grafici dinamici per visualizzare le previsioni.
*   **Icone Meteo Dinamiche:** Aggiornare le icone meteo nella UI in base alle condizioni meteorologiche per un'esperienza visiva più ricca.
*   **Personalizzazione:** Supportare unità di misura alternate (Fahrenheit, km/h) selezionabili dall'utente.
*   **Gestione Avanzata della Cache:** Implementare strategie di caching più sofisticate per ottimizzare ulteriormente le prestazioni.
*   **Test E2E (End-to-End):** Estendere la copertura dei test per includere scenari completi dall'interfaccia utente al backend.

## Licenza
Questo progetto è distribuito sotto licenza MIT.
