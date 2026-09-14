FROM eclipse-temurin:21-jre

WORKDIR /customer-service

COPY target/customer.jar customer.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "customer.jar"]