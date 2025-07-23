# Fase de construcción (Build)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Fase de ejecución (Runtime)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno (sobrescribibles con `docker run -e`)
ENV APP_SECURITY_API_KEY="clave-secreta-docker" \
    APP_SECURITY_API_KEY_HEADER="X-API-Key" \
    SPRING_DATASOURCE_URL="jdbc:postgresql://postgres:5432/inventory_db" \
    SPRING_DATASOURCE_USERNAME="postgres" \
    SPRING_DATASOURCE_PASSWORD="123456" \
    SPRING_PROFILES_ACTIVE="prod"

# Puerto expuesto y comando de inicio
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]