FROM  eclipse-temurin:21-jre

WORKDIR /discovery-service

COPY target/discovery.jar discovery.jar

EXPOSE 8761

ENTRYPOINT [ "java","-jar","discovery.jar" ]