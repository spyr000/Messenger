# syntax=docker/dockerfile:experimental
FROM openjdk:17-jdk-alpine as builder

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw ./mvnw package -Dmaven.test.skip

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder target/*.jar server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "server.jar"]