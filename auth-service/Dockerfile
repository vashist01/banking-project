# =========================
# Stage 1: Build
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /auth-service

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


# =========================
# Stage 2: Run
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /auth-service

COPY --from=build /auth-service/target/*.jar auth.jar

EXPOSE 8001

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=25.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "auth.jar"]