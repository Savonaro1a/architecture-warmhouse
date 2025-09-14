package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class TemperatureService {

    Random random = new Random();

    public TemperatureData generateTemperatureData(String location, String sensorId) {
        double value = 18.0 + random.nextDouble() * 10.0;

        if (location.isEmpty()) {
            switch(sensorId) {
                case "1":
                    location = "Living Room";
                    break;
                case "2":
                    location = "Bedroom";
                    break;
                case "3":
                    location = "Kitchen";
                    break;
                default:
                    location = "Unknown";
            }
        }

        if (sensorId.isEmpty()) {
            switch(location) {
                case "Living Room":
                    sensorId = "1";
                    break;
                case "Bedroom":
                    sensorId = "2";
                    break;
                case "Kitchen":
                    sensorId = "3";
                    break;
                default:
                    sensorId = "0";
            }
        }

        return new TemperatureData(
                value,
                "°C",
                LocalDateTime.now(),
                location,
                "active",
                sensorId,
                "temperature",
                "Temperature sensor in " + location
        );
    }
}
