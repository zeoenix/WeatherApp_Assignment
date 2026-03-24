package com.weatherapp.service;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import com.weatherapp.utils.SimpleJsonParser;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service to handle API integration with OpenWeatherMap.
 */
public class WeatherService {
    
    // Loaded securely from ignored properties file
    private static String API_KEY = "YOUR_API_KEY_HERE"; 
    
    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            Properties props = new Properties();
            props.load(in);
            String key = props.getProperty("API_KEY");
            if (key != null && !key.trim().isEmpty()) {
                API_KEY = key.trim();
            }
        } catch (Exception e) {
            System.out.println("Notice: config.properties missing. API key must be provided.");
        }
    }

    // Base URLs
    private static final String WEATHER_URL_FORMAT = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";
    private static final String FORECAST_URL_FORMAT = "https://api.openweathermap.org/data/2.5/forecast?q=%s&appid=%s";
    private static final String GEO_URL_FORMAT = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=5&appid=%s";

    private final HttpClient httpClient;

    public WeatherService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public WeatherData getCurrentWeather(String location) throws Exception {
        if ("YOUR_API_KEY_HERE".equals(API_KEY) || API_KEY.trim().isEmpty()) {
            throw new Exception("Missing OpenWeatherMap API Key.\nPlease insert your key inside config.properties!");
        }

        String url = String.format(WEATHER_URL_FORMAT, URLEncoder.encode(location, StandardCharsets.UTF_8), API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new Exception("API Check failed or Location not found! Code: " + response.statusCode() + " " + response.body());
        }

        return parseWeatherData(response.body());
    }

    public List<String> getSuggestions(String query) throws Exception {
        if ("YOUR_API_KEY_HERE".equals(API_KEY) || API_KEY.trim().isEmpty() || query.length() < 2) {
            return new ArrayList<>(); 
        }

        String url = String.format(GEO_URL_FORMAT, URLEncoder.encode(query, StandardCharsets.UTF_8), API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            return new ArrayList<>();
        }

        SimpleJsonParser parser = new SimpleJsonParser(response.body());
        Object parsed = parser.parse();
        List<String> suggestions = new ArrayList<>();

        if (parsed instanceof List) {
            List<?> list = (List<?>) parsed;
            for (Object obj : list) {
                if (obj instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) obj;
                    String name = (String) map.get("name");
                    String state = (String) map.get("state");
                    String country = (String) map.get("country");
                    
                    String suggestion = name;
                    if (state != null && !state.isEmpty()) suggestion += ", " + state;
                    if (country != null && !country.isEmpty()) suggestion += ", " + country;
                    
                    if (!suggestions.contains(suggestion)) {
                        suggestions.add(suggestion);
                    }
                }
            }
        }
        return suggestions;
    }

    public List<ForecastData> getForecast(String location) throws Exception {
        if ("YOUR_API_KEY_HERE".equals(API_KEY) || API_KEY.trim().isEmpty()) {
            throw new Exception("Missing OpenWeatherMap API Key.\nPlease insert your key inside config.properties!");
        }

        String url = String.format(FORECAST_URL_FORMAT, URLEncoder.encode(location, StandardCharsets.UTF_8), API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Forecast request failed! Code: " + response.statusCode());
        }

        return parseForecastData(response.body());
    }

    @SuppressWarnings("unchecked")
    private WeatherData parseWeatherData(String json) {
        SimpleJsonParser parser = new SimpleJsonParser(json);
        Map<String, Object> map = (Map<String, Object>) parser.parse();

        WeatherData data = new WeatherData();
        data.setCityName((String) map.get("name"));
        
        Map<String, Object> main = (Map<String, Object>) map.get("main");
        data.setTemperature(toDouble(main.get("temp")));
        data.setHumidity(toDouble(main.get("humidity")));

        Map<String, Object> wind = (Map<String, Object>) map.get("wind");
        data.setWindSpeed(toDouble(wind.get("speed")));

        List<Object> weatherList = (List<Object>) map.get("weather");
        Map<String, Object> weather = (Map<String, Object>) weatherList.get(0);
        data.setCondition((String) weather.get("main"));
        data.setIconCode((String) weather.get("icon"));

        data.setTimestamp(toLong(map.get("dt")));
        data.setTimezone(((Number) map.get("timezone")).intValue());

        Map<String, Object> sys = (Map<String, Object>) map.get("sys");
        data.setSunrise(toLong(sys.get("sunrise")));
        data.setSunset(toLong(sys.get("sunset")));

        return data;
    }

    @SuppressWarnings("unchecked")
    private List<ForecastData> parseForecastData(String json) {
        SimpleJsonParser parser = new SimpleJsonParser(json);
        Map<String, Object> map = (Map<String, Object>) parser.parse();

        List<Object> list = (List<Object>) map.get("list");
        List<ForecastData> forecasts = new ArrayList<>();

        for (int i = 0; i < list.size() && i < 8; i++) { // Get next 24 hours (8 records of 3-hours)
            Map<String, Object> item = (Map<String, Object>) list.get(i);
            String dtText = (String) item.get("dt_txt");

            Map<String, Object> main = (Map<String, Object>) item.get("main");
            double temp = toDouble(main.get("temp"));

            List<Object> weatherList = (List<Object>) item.get("weather");
            Map<String, Object> weather = (Map<String, Object>) weatherList.get(0);
            String condition = (String) weather.get("main");
            String icon = (String) weather.get("icon");

            forecasts.add(new ForecastData(dtText, temp, condition, icon));
        }

        return forecasts;
    }

    private double toDouble(Object num) {
        if (num instanceof Number) {
            return ((Number) num).doubleValue();
        }
        return 0;
    }

    private long toLong(Object num) {
        if (num instanceof Number) {
            return ((Number) num).longValue();
        }
        return 0;
    }
}
