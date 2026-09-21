# https://docs.docker.com/reference/dockerfile/#syntax
# syntax=docker/dockerfile:1

################################################################################
# BUILD JAR

FROM maven:3.9.16-eclipse-temurin-25-alpine AS builder

WORKDIR /builder

ADD . /builder

# https://maven.apache.org/ref/current/maven-embedder/cli.html
ARG PROJECT_VERSION
RUN mvn --quiet --batch-mode --update-snapshots --fail-fast -DskipTests -Drevision=${PROJECT_VERSION} package

################################################################################
# BUILD IMAGE

FROM eclipse-temurin:25.0.4_7-jre-alpine

# Upgrade OS packages to apply latest security patches
RUN apk upgrade --no-cache

# add non-root user to run the app
# https://spring.io/guides/gs/spring-boot-docker
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /fdp

COPY --from=builder /builder/target/fdp-spring-boot.jar /fdp/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
