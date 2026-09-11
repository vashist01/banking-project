FROM eclipse-temurin:21-jre

WORKDIR /auth-service

COPY --from=build /app/target/auth.jar auth.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "auth.jar"]