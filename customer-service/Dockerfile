# ==========================================
# CUSTOMER SERVICE - JAVA 21
# ==========================================

# Java 21 runtime image
# Maven build is already performed by Jenkins
FROM eclipse-temurin:21-jre

# Application directory inside container
WORKDIR /customer-service

# Copy JAR created by Jenkins Maven Build
# Jenkins creates this inside customer-service/target/
COPY target/*.jar customer.jar

# Spring Boot Customer Service port
EXPOSE 8082

# Start the application
ENTRYPOINT ["java", "-jar", "/customer-service/customer.jar"]