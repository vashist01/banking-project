FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /config-server

COPY --from=build /app/target/*.jar config-server.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "config-server.jar"]