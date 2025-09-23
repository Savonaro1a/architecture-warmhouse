```puml
@startuml C4_Component_Diagram_Device_Management
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml

title Диаграмма компонентов: Device Management Service

Person(user, "Пользователь", "Управляет устройствами")
Person(device, "Устройство", "Отправляет телеметрию и получает команды")

System_Boundary(device_management_service, "Device Management Service") {
' API Layer
Container_Boundary(api_layer, "API Layer") {
Component(device_controller, "Device Controller", "Go + Gin", "HTTP API для управления устройствами")
Component(device_status_controller, "Device Status Controller", "Go + Gin", "API для статуса устройств")
Component(device_metadata_controller, "Device Metadata Controller", "Go + Gin", "API для метаданных устройств")
}

    ' Business Logic Layer
    Container_Boundary(business_layer, "Business Logic Layer") {
        Component(device_service, "Device Service", "Go", "Бизнес-логика управления устройствами")
        Component(device_status_service, "Device Status Service", "Go", "Управление статусом устройств")
        Component(device_metadata_service, "Device Metadata Service", "Go", "Управление метаданными")
        Component(device_health_checker, "Device Health Checker", "Go", "Проверка состояния устройств")
        Component(device_validator, "Device Validator", "Go", "Валидация данных устройств")
    }
    
    ' Integration Layer
    Container_Boundary(integration_layer, "Integration Layer") {
        Component(device_repository, "Device Repository", "Go", "Абстракция доступа к данным")
        Component(device_status_repository, "Device Status Repository", "Go", "Работа со статусами")
        Component(device_metadata_repository, "Device Metadata Repository", "Go", "Работа с метаданными")
        Component(message_publisher, "Message Publisher", "Go", "Публикация событий")
        Component(device_connector, "Device Connector", "Go", "Подключение к устройствам")
    }
    
    ' Data Layer
    Container_Boundary(data_layer, "Data Layer") {
        ComponentDb(device_db, "Device Database", "PostgreSQL", "Хранение данных устройств")
        ComponentDb(device_status_cache, "Device Status Cache", "Redis", "Кэш статусов устройств")
        ComponentDb(device_metadata_cache, "Device Metadata Cache", "Redis", "Кэш метаданных")
    }
}

System_Ext(api_gateway, "API Gateway", "Маршрутизация запросов")
System_Ext(message_broker, "Message Broker", "Apache Kafka")
System_Ext(device_lifecycle_service, "Device Lifecycle Service", "Регистрация устройств")
System_Ext(control_service, "Control Service", "Отправка команд")
System_Ext(telemetry_service, "Telemetry Service", "Сбор телеметрии")

' Пользовательские запросы
Rel(user, api_gateway, "Управляет устройствами", "HTTPS")
Rel(api_gateway, device_controller, "API запросы", "HTTP/REST")

' Внутренние взаимодействия API Layer
Rel(device_controller, device_service, "Обработка запросов", "Function Call")
Rel(device_status_controller, device_status_service, "Обработка статусов", "Function Call")
Rel(device_metadata_controller, device_metadata_service, "Обработка метаданных", "Function Call")

' Взаимодействия Business Logic Layer
Rel(device_service, device_validator, "Валидация данных", "Function Call")
Rel(device_service, device_repository, "Операции с данными", "Function Call")
Rel(device_status_service, device_status_repository, "Работа со статусами", "Function Call")
Rel(device_metadata_service, device_metadata_repository, "Работа с метаданными", "Function Call")
Rel(device_health_checker, device_connector, "Проверка состояния", "Function Call")
Rel(device_health_checker, device_status_service, "Обновление статуса", "Function Call")

' Взаимодействия Integration Layer
Rel(device_repository, device_db, "SQL запросы", "SQL")
Rel(device_status_repository, device_status_cache, "Кэширование", "Redis Protocol")
Rel(device_metadata_repository, device_metadata_cache, "Кэширование", "Redis Protocol")
Rel(device_service, message_publisher, "Публикация событий", "Function Call")
Rel(device_connector, device, "MQTT/HTTP команды", "MQTT/HTTP")

' Внешние интеграции
Rel(message_publisher, message_broker, "Публикация событий", "Kafka Protocol")
Rel(device_lifecycle_service, device_service, "Регистрация устройств", "HTTP/REST")
Rel(control_service, device_connector, "Отправка команд", "Events/Async")
Rel(telemetry_service, device_status_service, "Обновление статуса", "Events/Async")

' Обратная связь от устройств
Rel(device, device_connector, "Телеметрия и статус", "MQTT/HTTP")
Rel(device_connector, device_status_service, "Обновление статуса", "Function Call")

@enduml
```