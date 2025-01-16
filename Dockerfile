FROM ubuntu:latest as build
RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY . .
RUN apt-get install maven -y
RUN mvn clean package

FROM openjdk:23-jdk-slim
EXPOSE 8080
COPY  --from=build /target/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]