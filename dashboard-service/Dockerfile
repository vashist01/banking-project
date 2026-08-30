FROM eclipse-temurin:17-jre

WORKDIR /dashbaord-service

COPY --from=build /app/target/*.jar dashboard.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "dashboard.jar"]