# Multi-stage Dockerfile for user-service

# 1) Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy root files that may be needed by Maven (parent pom, shared modules, protos)
COPY pom.xml ./
COPY services/user-service/pom.xml services/user-service/
RUN mvn -B -f services/user-service/pom.xml dependency:go-offline
COPY protos ./protos
COPY shared ./shared

# Copy only the user-service module sources
COPY services/user-service/src services/user-service/src

# Build only the user-service module using its own POM
RUN mvn -B -DskipTests -f services/user-service/pom.xml clean package

# 2) Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for better security
RUN addgroup -S codistrib && adduser -S codistrib -G codistrib

# Copy the built jar from the build stage
COPY --from=build /app/services/user-service/target/*.jar /app/app.jar

ARG USER_SERVICE_PORT
ARG AUTH_SERVICE_PORT

# Default Spring profile can be overridden by environment
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV USER_SERVICE_PORT=${USER_SERVICE_PORT}
ENV AUTH_SERVICE_PORT=${AUTH_SERVICE_PORT}

USER codistrib
EXPOSE ${USER_SERVICE_PORT}

# JVM opts can be passed via JAVA_OPTS env at runtime if needed
# Healthcheck
HEALTHCHECK --interval=30s \
            --timeout=10s \
            --start-period=60s \
            --retries=3 \
    CMD nc -z localhost ${USER_SERVICE_PORT} || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]