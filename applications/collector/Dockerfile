# ---- BUILD STAGE ----
    FROM gradle:8.0-jdk17 AS builder
    WORKDIR /app
    COPY . .                         
    WORKDIR /app/applications/collector    
    RUN gradle clean jar --no-daemon

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/applications/collector/build/libs/collector.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
