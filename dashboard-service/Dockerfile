FROM eclipse-temurin:17-jre

WORKDIR /dashbaord-service

COPY target/*.jar dashboad.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "dashboard.jar"]