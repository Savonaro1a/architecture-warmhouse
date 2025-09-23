package com.warmhouse.temperatureapi.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TemperatureService {

    private final Random random = new Random();
    private final ConcurrentHashMap<String, Double> locationBaseTemperatures = new ConcurrentHashMap<>();

    public TemperatureService() {
        // Initialize base temperatures for different locations
        locationBaseTemperatures.put("kitchen", 22.0);
        locationBaseTemperatures.put("living_room", 21.0);
        locationBaseTemperatures.put("bedroom", 20.0);
        locationBaseTemperatures.put("bathroom", 24.0);
        locationBaseTemperatures.put("garage", 18.0);
        locationBaseTemperatures.put("unknown", 21.5);
    }

    public double generateRandomTemperature(String location) {
        // Get base temperature for location or use default
        double baseTemp = locationBaseTemperatures.getOrDefault(
            location.toLowerCase(), 
            locationBaseTemperatures.get("unknown")
        );
        
        // Add random variation between -2.0 and +2.0 degrees
        double variation = (random.nextDouble() - 0.5) * 4.0;
        double temperature = baseTemp + variation;
        
        // Round to one decimal place
        return Math.round(temperature * 10.0) / 10.0;
    }
}
