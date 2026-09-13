FROM eclipse-temurin:21-jre

WORKDIR /customer-service

# Jenkins Maven Build se generated JAR copy hoga
COPY target/*.jar customer.jar

# Non-root application user
RUN useradd --system --uid 1001 customeruser

# Application directory ka ownership
RUN chown -R customeruser:customeruser /customer-service

# Java application non-root user se run hogi
USER customeruser

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/customer-service/customer.jar"]