FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /api-gateway

COPY --from=build /app/target/*.jar api-gateway.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]