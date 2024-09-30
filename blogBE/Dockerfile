# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build-stage
COPY --chown=maven:maven . /blogBE
WORKDIR /blogBE

#skip task: test
RUN mvn clean package -DskipTests

FROM openjdk:21-slim

COPY --from=build-stage /blogBE/target/*.jar /blogBE/app.jar
ENTRYPOINT ["java", "-jar", "/blogBE/app.jar"]
