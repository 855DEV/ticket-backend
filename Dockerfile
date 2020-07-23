FROM openjdk:14-jdk-alpine
WORKDIR /app
COPY target/*.jar /app
ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
