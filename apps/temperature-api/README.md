# Temperature API

Java Spring Boot приложение для имитации работы удалённого датчика температуры.

## Описание

Temperature API предоставляет REST API для получения случайных значений температуры для различных локаций в умном доме.

## Возможности

- Получение температуры для различных локаций (кухня, гостиная, спальня, ванная, гараж)
- Генерация случайных значений температуры с базовыми значениями для каждой локации
- Health check endpoint для мониторинга состояния сервиса
- Docker контейнеризация

## API Endpoints

### GET /api/temperature

Получение температуры для указанной локации.

**Параметры:**
- `location` (optional) - локация для получения температуры. Если не указана, используется "unknown"

**Примеры запросов:**
```
GET /api/temperature?location=kitchen
GET /api/temperature?location=living_room
GET /api/temperature
```

**Пример ответа:**
```json
{
    "location": "kitchen",
    "temperature": 22.3,
    "timestamp": "2024-01-15T10:30:45",
    "unit": "celsius"
}
```

### GET /api/health

Проверка состояния сервиса.

**Пример ответа:**
```json
{
    "status": "UP",
    "service": "temperature-api"
}
```

## Локации и базовые температуры

- kitchen: 22.0°C
- living_room: 21.0°C
- bedroom: 20.0°C
- bathroom: 24.0°C
- garage: 18.0°C
- unknown: 21.5°C (по умолчанию)

Каждое значение температуры генерируется как базовое значение + случайное отклонение от -2.0 до +2.0 градусов.

## Запуск

### Локально

```bash
./mvnw spring-boot:run
```

### Docker

```bash
docker build -t temperature-api .
docker run -p 8081:8081 temperature-api
```

### Docker Compose

```bash
cd ..
docker-compose up temperature-api
```

## Порт

По умолчанию приложение запускается на порту **8081**.

## Технологии

- Java 17
- Spring Boot 3.2.0
- Maven
- Docker
