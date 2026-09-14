FROM eclipse-temurin:21-jre

WORKDIR /discovery-service

COPY target/*.jar discovery.jar

RUN groupadd --system --gid 1001 discoveryuser && \
    useradd --system --uid 1001 --gid 1001 discoveryuser

RUN chown -R 1001:1001 /discovery-service

USER 1001

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/discovery-service/discovery.jar"]