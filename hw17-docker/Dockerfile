FROM maven:3.9.14-eclipse-temurin-17 AS builder
WORKDIR /app

COPY src ./src
COPY pom.xml .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/hw17-docker-0.0.1-SNAPSHOT.jar hw17-docker.jar
ENTRYPOINT ["java", "-jar", "hw17-docker.jar"]