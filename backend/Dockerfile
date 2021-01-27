FROM adoptopenjdk/openjdk11:jre-11.0.3_7-alpine

WORKDIR /opt/config

COPY application/target/*.jar ./application.jar
COPY service/target/classes/*.json ./credentials.json

RUN apk add  --no-cache ffmpeg

ENTRYPOINT java -jar -Dspring.profiles.active=prod application.jar
