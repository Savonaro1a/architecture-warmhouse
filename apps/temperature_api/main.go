package main

import (
    "math/rand"
    "net/http"
    "time"

    "github.com/gin-gonic/gin"
)

func main() {
    r := gin.Default()

    r.GET("/temperature", func(c *gin.Context) {
        location := c.Query("location")
        sensorID := c.Query("sensorId")

        // Если location пустой, выбираем по sensorID
        if location == "" {
            switch sensorID {
            case "1":
                location = "Living Room"
            case "2":
                location = "Bedroom"
            case "3":
                location = "Kitchen"
            default:
                location = "Unknown"
            }
        }

        // Если sensorID пустой, выбираем по location
        if sensorID == "" {
            switch location {
            case "Living Room":
                sensorID = "1"
            case "Bedroom":
                sensorID = "2"
            case "Kitchen":
                sensorID = "3"
            default:
                sensorID = "0"
            }
        }

        // Генерируем случайную температуру от 15 до 30 градусов для примера
        rand.Seed(time.Now().UnixNano())
        temperature := 15 + rand.Float64()*(30-15)

        c.JSON(http.StatusOK, gin.H{
            "location":    location,
            "sensorId":    sensorID,
            "temperature": temperature,
        })
    })

    r.Run(":8081") // Запуск на порту 8081
}
