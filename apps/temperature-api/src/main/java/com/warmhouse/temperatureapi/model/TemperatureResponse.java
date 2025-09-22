package com.warmhouse.temperatureapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TemperatureResponse {
    
    @JsonProperty("location")
    private String location;
    
    @JsonProperty("temperature")
    private double temperature;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("unit")
    private String unit;

    public TemperatureResponse() {}

    public TemperatureResponse(String location, double temperature) {
        this.location = location;
        this.temperature = temperature;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.unit = "celsius";
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "TemperatureResponse{" +
                "location='" + location + '\'' +
                ", temperature=" + temperature +
                ", timestamp='" + timestamp + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
