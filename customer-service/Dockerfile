# ==========================================
# CUSTOMER SERVICE - JAVA 21
# ==========================================

# Java 21 runtime image
# Maven build is already performed by Jenkins
FROM eclipse-temurin:21-jre

WORKDIR /customer-service

COPY target/auth.jar customer.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "customer.jar"]