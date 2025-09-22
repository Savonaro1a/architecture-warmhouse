```puml
 @startuml C4_Container_Diagram_Current_Monolith
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

title Контейнерная диаграмма текущего монолита "Тёплый дом"

Person(homeowner, "Владелец дома", "Управляет отоплением")

System_Boundary(monolith, "Smart Home Monolith (Go)") {
    Container(gin_router, "Gin Router", "Gin Framework", "HTTP маршрутизация")
    Container(sensor_handlers, "Sensor Handlers", "Go", "CRUD операции с датчиками")
    Container(temperature_service, "Temperature Service", "Go", "Интеграция с внешним API")
    Container(db_layer, "Database Layer", "Go + pgx", "Работа с PostgreSQL")
    ContainerDb(postgres, "PostgreSQL", "PostgreSQL 16", "Хранение данных датчиков")
}

System_Ext(temperature_api, "Temperature API", "Внешний сервис температуры")
System_Ext(heating_system, "Home Heating System", "Система отопления")
System_Ext(temperature_sensors, "Temperature Sensors", "Датчики температуры")

' Пользователь взаимодействует с монолитом
Rel(homeowner, gin_router, "Управляет отоплением", "HTTPS")

' Внутренние взаимодействия в монолите
Rel(gin_router, sensor_handlers, "Маршрутизация запросов", "Function Call")
Rel(sensor_handlers, temperature_service, "Получение данных температуры", "Function Call")
Rel(sensor_handlers, db_layer, "Операции с датчиками", "Function Call")
Rel(temperature_service, db_layer, "Обновление данных", "Function Call")
Rel(db_layer, postgres, "SQL запросы", "SQL")

' Внешние взаимодействия
Rel(temperature_service, temperature_api, "HTTP запросы", "HTTP/REST")
Rel(gin_router, heating_system, "Команды управления", "HTTP/MQTT")
Rel(temperature_sensors, gin_router, "Данные температуры", "HTTP/MQTT")

' Обратная связь
Rel(heating_system, gin_router, "Статус команд", "HTTP/MQTT")
@enduml
```