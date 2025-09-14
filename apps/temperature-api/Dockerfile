# Используем базовую JDK-образ Ubuntu
FROM openjdk:17-jdk-slim-buster

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем артефакт сборки приложения внутрь образа
COPY target/*.jar app.jar

# Объявляем переменную окружения для порта
ENV PORT=8081

# Открываем порт 8081 наружу
EXPOSE ${PORT}

# Запускаем приложение с указанием порта
CMD ["java", "-Dserver.port=${PORT}", "-jar", "/app/app.jar"]