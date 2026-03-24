package com.weatherapp.model;

public class WeatherData {
    private String cityName;
    private double temperature; // in Kelvin initially, will be converted by the UI
    private double humidity;
    private double windSpeed; // in m/s
    private String condition;
    private String iconCode;
    private long timestamp;
    private long sunrise;
    private long sunset;
    private int timezone;

    // Getters and Setters
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getIconCode() { return iconCode; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public long getSunrise() { return sunrise; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }

    public long getSunset() { return sunset; }
    public void setSunset(long sunset) { this.sunset = sunset; }

    public int getTimezone() { return timezone; }
    public void setTimezone(int timezone) { this.timezone = timezone; }
}
