#
# The MIT License
# Copyright © 2016-2024 FAIR Data Team
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#

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
