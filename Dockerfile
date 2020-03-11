# Builder
FROM maven:3-jdk-8-slim AS builder

ADD . ./

RUN mvn -Ppackage-all -DskipTests clean install -U

# App
FROM openjdk:8-jre-slim

EXPOSE 8080

#CMD [""]