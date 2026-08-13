FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /APP
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

#stage 2

FROM eclipse-temurin:17

WORKDIR /credit-service

COPY --from=build app/target/*.jar credit.jar

EXPOSE 8009

ENTRYPOINT ["java","-jar","credit.jar"]