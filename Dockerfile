FROM openjdk:23-jdk-slim
RUN mkdir /app
WORKDIR /app
COPY target/*.jar /app/app.jar
CMD [ "java", "-jar", "/app/app.jar" ]