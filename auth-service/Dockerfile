FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /auth-service

COPY --from=build /app/target/*.jar auth.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "auth.jar"]