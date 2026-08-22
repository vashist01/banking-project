FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /APP
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

#stage 2
FROM eclipse-temurin:17

WORKDIR /payment-service

COPY --from=build app/target/*.jar payment.jar

# Copy OpenTelemetry Java Agent
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar

EXPOSE 8008

ENTRYPOINT ["java", \
    "-javaagent:/payment-service/opentelemetry-javaagent.jar", \
    "-jar", \
    "payment.jar"]