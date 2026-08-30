FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /pp

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17

WORKDIR /dashboard-service

COPY --from=build app/target/*.jar dashbaord.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","dashboard-jar"]