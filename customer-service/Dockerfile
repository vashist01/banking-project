FROM eclipse-temurin:21-jre

WORKDIR /customer-service

# Jenkins Maven Build se generated JAR copy hoga
COPY target/*.jar customer.jar

# Create dedicated non-root group with GID 1001
RUN groupadd --system --gid 1001 customeruser && \
    useradd --system --uid 1001 --gid 1001 customeruser

# Application directory ka ownership
RUN chown -R 1001:1001 /customer-service

# Java application non-root user se run hogi
USER 1001

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/customer-service/customer.jar"]