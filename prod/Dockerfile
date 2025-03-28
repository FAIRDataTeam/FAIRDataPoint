FROM eclipse-temurin:21-jdk-alpine

WORKDIR /fdp
ADD fdp-prod.jar /fdp/app.jar
ENTRYPOINT java -jar app.jar --spring.profiles.active=production --spring.config.location=classpath:/application.yml,classpath:/application-production.yml,file:/fdp/application.yml
