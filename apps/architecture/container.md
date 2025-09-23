```puml
@startuml C4_Container_Diagram_SmartHome_Ecosystem
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

title Контейнерная диаграмма экосистемы "Тёплый дом"

Person(user, "Пользователь", "Владелец дома, самостоятельно подключает устройства")

System_Boundary(smarthome_ecosystem, "Экосистема 'Тёплый дом'") {
Container_Boundary(api_layer, "API Layer") {
Container(api_gateway, "API Gateway", "Kong/Nginx", "Маршрутизация, аутентификация, rate limiting")
Container(web_app, "Web Application", "React/Vue.js", "Пользовательский интерфейс")
Container(mobile_app, "Mobile Application", "React Native/Flutter", "Мобильное приложение")
}

    Container_Boundary(identity_domain, "Identity Domain") {
        Container(identity_service, "Identity Service", "Go/Java", "Управление пользователями и домами")
        ContainerDb(identity_db, "Identity Database", "PostgreSQL", "Пользователи, дома, роли")
    }
    
    Container_Boundary(device_domain, "Device Domain") {
        Container(device_catalog, "Device Catalog Service", "Go/Java", "Каталог устройств и типов")
        Container(device_lifecycle, "Device Lifecycle Service", "Go/Java", "Регистрация и управление устройствами")
        Container(device_management, "Device Management Service", "Go/Java", "Статус и метаданные устройств")
        ContainerDb(device_db, "Device Database", "PostgreSQL", "Устройства, типы, статусы")
    }
    
    Container_Boundary(control_domain, "Control Domain") {
        Container(control_service, "Control Service", "Go/Java", "Отправка команд на устройства")
        Container(telemetry_service, "Telemetry Service", "Go/Java", "Сбор и хранение телеметрии")
        ContainerDb(telemetry_db, "Telemetry Database", "InfluxDB/TimescaleDB", "Временные ряды телеметрии")
        Container(telemetry_cache, "Telemetry Cache", "Redis", "Кэш для быстрого доступа")
    }
    
    Container_Boundary(automation_domain, "Automation Domain") {
        Container(automation_service, "Automation Service", "Go/Java", "Сценарии и автоматизация")
        Container(rule_engine, "Rule Engine", "Drools/OpenL", "Обработка правил")
        ContainerDb(automation_db, "Automation Database", "PostgreSQL", "Сценарии, правила, события")
    }
    
    Container_Boundary(integration_layer, "Integration Layer") {
        Container(message_broker, "Message Broker", "Apache Kafka/RabbitMQ", "Асинхронная коммуникация")
        Container(device_connector, "Device Connector", "Go/Java", "Подключение к устройствам")
        Container(notification_service, "Notification Service", "Go/Java", "SMS, email, push")
    }
}

System_Ext(partner_devices, "Устройства партнёров", "Термостаты, лампы, замки, камеры")
System_Ext(external_apis, "Внешние API", "Погода, энергетика")

' Пользовательские интерфейсы
Rel(user, web_app, "Самостоятельно подключает устройства и управляет домом", "HTTPS")
Rel(user, mobile_app, "Самостоятельно подключает устройства и управляет домом", "HTTPS")

' API Gateway взаимодействует с приложениями
Rel(web_app, api_gateway, "API запросы", "HTTPS/REST")
Rel(mobile_app, api_gateway, "API запросы", "HTTPS/REST")

' API Gateway к сервисам
Rel(api_gateway, identity_service, "Аутентификация", "HTTP/REST")
Rel(api_gateway, device_catalog, "Каталог устройств", "HTTP/REST")
Rel(api_gateway, device_lifecycle, "Регистрация устройств", "HTTP/REST")
Rel(api_gateway, device_management, "Управление устройствами", "HTTP/REST")
Rel(api_gateway, control_service, "Отправка команд", "HTTP/REST")
Rel(api_gateway, telemetry_service, "Получение телеметрии", "HTTP/REST")
Rel(api_gateway, automation_service, "Управление сценариями", "HTTP/REST")

' Identity Service
Rel(identity_service, identity_db, "Читает/записывает", "SQL")

' Device Services
Rel(device_catalog, device_db, "Читает/записывает", "SQL")
Rel(device_lifecycle, device_db, "Читает/записывает", "SQL")
Rel(device_management, device_db, "Читает/записывает", "SQL")

' Control Services
Rel(control_service, device_connector, "Отправляет команды", "MQTT/Async")
Rel(telemetry_service, telemetry_db, "Читает/записывает", "SQL/InfluxQL")
Rel(telemetry_service, telemetry_cache, "Кэширует данные", "Redis Protocol")

' Automation Service
Rel(automation_service, rule_engine, "Обрабатывает правила", "HTTP/REST")
Rel(automation_service, automation_db, "Читает/записывает", "SQL")
Rel(automation_service, message_broker, "Публикует события", "Kafka Protocol")

' Межсервисное взаимодействие через Message Broker
Rel(device_management, message_broker, "Публикует события статуса", "Kafka Protocol")
Rel(telemetry_service, message_broker, "Публикует телеметрию", "Kafka Protocol")
Rel(automation_service, message_broker, "Подписывается на события", "Kafka Protocol")
Rel(control_service, message_broker, "Публикует команды", "Kafka Protocol")
Rel(notification_service, message_broker, "Подписывается на уведомления", "Kafka Protocol")

' Интеграция с внешними системами
Rel(device_connector, partner_devices, "Управляет устройствами", "MQTT/Async/CoAP")
Rel(automation_service, external_apis, "Получает внешние данные", "HTTP/REST")
Rel(notification_service, user, "Отправляет уведомления", "SMS/Email/Push/Async")

@enduml
```
