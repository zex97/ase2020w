FROM adoptopenjdk/openjdk11:jre-11.0.3_7-alpine

WORKDIR /opt/config

COPY rest-api/target/*.jar ./application.jar

ENTRYPOINT java -jar application.jar
