# https://docs.docker.com/reference/dockerfile/#syntax
# syntax=docker/dockerfile:1

################################################################################
# BUILD JAR

FROM maven:3-eclipse-temurin-21-alpine AS builder

WORKDIR /builder

ADD . /builder

# https://maven.apache.org/ref/current/maven-embedder/cli.html
RUN mvn --quiet --batch-mode --update-snapshots --fail-fast -DskipTests package

################################################################################
# BUILD IMAGE

FROM eclipse-temurin:21-jdk-alpine

# add non-root user to run the app
# https://spring.io/guides/gs/spring-boot-docker
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /fdp

COPY --from=builder /builder/target/fdp-spring-boot.jar /fdp/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
