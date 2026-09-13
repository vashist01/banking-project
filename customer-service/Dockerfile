FROM eclipse-temurin:17

WORKDIR /customer-service

COPY --from=build /app/target/*.jar customer.jar
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar

RUN useradd --system --uid 1001 customeruser

RUN chown -R customeruser:customeruser /customer-service

USER 1001

EXPOSE 8082

ENTRYPOINT ["java", \
    "-javaagent:/customer-service/opentelemetry-javaagent.jar", \
    "-jar", \
    "/customer-service/customer.jar"]