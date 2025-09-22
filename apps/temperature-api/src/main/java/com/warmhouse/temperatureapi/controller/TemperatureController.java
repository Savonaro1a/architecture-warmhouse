package com.warmhouse.temperatureapi.controller;

import com.warmhouse.temperatureapi.model.TemperatureResponse;
import com.warmhouse.temperatureapi.service.TemperatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TemperatureController {

    private final TemperatureService temperatureService;

    @Autowired
    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping("/temperature")
    public ResponseEntity<TemperatureResponse> getTemperature(
            @RequestParam(value = "location", required = false, defaultValue = "unknown") String location) {
        
        double temperature = temperatureService.generateRandomTemperature(location);
        TemperatureResponse response = new TemperatureResponse(location, temperature);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "temperature-api"));
    }
}
