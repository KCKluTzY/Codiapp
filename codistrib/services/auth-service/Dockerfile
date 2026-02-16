# Multi-stage Dockerfile for auth-service

# 1) Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy root files that may be needed by Maven (parent pom, shared modules, protos)
COPY pom.xml ./
COPY services/auth-service/pom.xml services/auth-service/
RUN mvn -B -f services/auth-service/pom.xml dependency:go-offline
COPY protos ./protos
COPY shared ./shared

# Copy only the auth-service module sources
COPY services/auth-service/src services/auth-service/src

# Build only the auth-service module using its own POM
RUN mvn -B -DskipTests -f services/auth-service/pom.xml clean package

# 2) Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for better security
RUN addgroup -S codistrib && adduser -S codistrib -G codistrib

# Copy the built jar from the build stage
COPY --from=build /app/services/auth-service/target/*.jar /app/app.jar

ARG AUTH_SERVICE_PORT

# Default Spring profile can be overridden by environment
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV AUTH_SERVICE_PORT=${AUTH_SERVICE_PORT}

USER codistrib
EXPOSE ${AUTH_SERVICE_PORT}

# JVM opts can be passed via JAVA_OPTS env at runtime if needed
# Healthcheck
HEALTHCHECK --interval=30s \
            --timeout=10s \
            --start-period=60s \
            --retries=3 \
    CMD nc -z localhost ${AUTH_SERVICE_PORT} || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]