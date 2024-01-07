FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
COPY ./ /tmp
RUN mvn -f /tmp/pom.xml clean package -DskipTests

FROM eclipse-temurin:21.0.1_12-jre
COPY --from=builder /tmp/target/veepeenet-telegram-bot-0.1.0.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
