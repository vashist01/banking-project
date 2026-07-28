# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17

WORKDIR /auth-service

COPY --from=build /app/target/*.jar auth.jar

EXPOSE 8008

ENTRYPOINT ["java","-jar","auth.jar"]