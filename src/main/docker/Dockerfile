# Multi-stage build para uma imagem final leve
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
COPY --from=build /usr/src/app/target/quarkus-app/ /deployments/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]