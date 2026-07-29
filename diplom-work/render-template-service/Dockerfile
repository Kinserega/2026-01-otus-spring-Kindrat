FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /workspace

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:17-jre-jammy

WORKDIR /application

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/target/*.jar application.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "application.jar"]