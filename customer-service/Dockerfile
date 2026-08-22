FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /APP
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

#stage 2
FROM eclipse-temurin:17

WORKDIR /customer-service

COPY --from=build app/target/*.jar customer.jar

# Copy OpenTelemetry Java Agent
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar

EXPOSE 8008

ENTRYPOINT ["java", \
    "-javaagent:/customer-service/opentelemetry-javaagent.jar", \
    "-jar", \
    "customer.jar"]