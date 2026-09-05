FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar auth.jar

EXPOSE 8001

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=25.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "auth.jar"]