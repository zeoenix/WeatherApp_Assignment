package com.weatherapp.model;

public class ForecastData {
    private String datetime;
    private double temperature;
    private String condition;
    private String iconCode;

    public ForecastData(String datetime, double temperature, String condition, String iconCode) {
        this.datetime = datetime;
        this.temperature = temperature;
        this.condition = condition;
        this.iconCode = iconCode;
    }

    public String getDatetime() { return datetime; }
    public double getTemperature() { return temperature; }
    public String getCondition() { return condition; }
    public String getIconCode() { return iconCode; }
}
