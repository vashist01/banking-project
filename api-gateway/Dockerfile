FROM eclipse-temurin:21-jre

WORKDIR /gateway

COPY target/*.jar gateway.jar

RUN groupadd --system --gid 1001 gatewayuser && \
    useradd --system --uid 1001 --gid 1001 gatewayuser

RUN chown -R 1001:1001 /gateway

USER 1001

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/api-gateway/gateway.jar"]