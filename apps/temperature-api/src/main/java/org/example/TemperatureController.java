package org.example;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class TemperatureController {

    @Autowired
    private TemperatureService temperatureService;

    @GetMapping(path = "/health")
    public void sayOk() {
        ResponseEntity.ok();
    }

    @GetMapping("/temperature")
    public TemperatureData getTemperatureByLocation(@RequestParam(required=true) String location) {
        if (location.isBlank())
            throw new IllegalArgumentException("Location is required");

        return temperatureService.generateTemperatureData(location, "");
    }

    @GetMapping("/temperature/{id}")
    public TemperatureData getTemperatureBySensorId(@PathVariable String id) {
        if (id.isBlank())
            throw new IllegalArgumentException("Sensor ID is required");

        return temperatureService.generateTemperatureData("", id);
    }
}
