# Use Eclipse Temurin JDK 21 as the base image
FROM eclipse-temurin:21-alpine

# Set working directory inside the container
WORKDIR /apps

# Install curl (needed for downloading Cloud SQL Proxy)
RUN apk add --no-cache curl

# Download and install Cloud SQL Proxy
RUN curl -o cloud_sql_proxy https://storage.googleapis.com/cloud-sql-connectors/cloud-sql-proxy/v2.8.0/cloud-sql-proxy.linux.amd64 && \
    chmod +x cloud_sql_proxy

# Copy application JAR files
COPY ./applications/app/build/libs/app.jar .
COPY ./applications/analyzer/build/libs/analyzer.jar .
COPY ./applications/collector/build/libs/collector.jar .

# Copy shell scripts
COPY ./bin/app.sh ./app.sh
COPY ./bin/analyze.sh ./analyze.sh
COPY ./bin/collect.sh ./collect.sh

RUN mkdir -p /cloudsql
RUN apk add postgresql-client

# Then in your startup script or CMD
# ./cloud_sql_proxy --unix-socket=/cloudsql ${INSTANCE_CONNECTION_NAME} &

# Set environment variables for Cloud SQL Proxy (override in deployment)
ENV INSTANCE_CONNECTION_NAME="s25-fse-team9:us-central1:gcp-cloud-starter"
ENV DB_USER="starter"
ENV DB_PASSWORD="starter"
ENV DB_NAME="starter_development"

# Instead of looking for startup.sh, run the commands directly
CMD ./cloud_sql_proxy --unix-socket=/cloudsql ${INSTANCE_CONNECTION_NAME} & sleep 2 && ./app.sh