# ---- Stage 1: Build (if you want multi-stage, uncomment below) ----
# FROM gradle:8.2.1-jdk21 AS builder
# WORKDIR /home/gradle/project
# COPY . .
# RUN gradle clean :app:shadowJar --no-daemon

# ---- Stage 2: Run ----
    FROM eclipse-temurin:21-jre
    WORKDIR /app
    
    # Copy the JAR from build context
    COPY build/libs/app.jar app.jar
    
    # App listens on 8080 by default (Ktor)
    EXPOSE 8080
    
    # Pass JVM options if needed (e.g. -Xmx512m)
    ENTRYPOINT ["java","-jar","app.jar"]
    