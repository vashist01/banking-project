# ============================================================
# STAGE 1: BUILD
# ============================================================

FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# ============================================================
# STAGE 2: RUNTIME
# ============================================================

FROM eclipse-temurin:21-jre

WORKDIR /customer-service

COPY --from=build /app/target/*.jar /customer-service/customer.jar


# Customer Service runs on port 8082
EXPOSE 8082


# Start Customer Service
ENTRYPOINT ["java", \
    "-jar", \
    "/customer-service/customer.jar"]