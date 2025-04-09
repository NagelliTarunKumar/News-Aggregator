# ---- BUILD STAGE ----
    FROM gradle:8.0-jdk17 AS builder
    WORKDIR /app
    COPY . .                         
    WORKDIR /app/applications/analyzer    
    RUN gradle clean jar --no-daemon
    
    # ---- RUNTIME STAGE ----
    FROM openjdk:17
    WORKDIR /app
    COPY --from=builder /app/applications/analyzer/build/libs/analyzer.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]
    