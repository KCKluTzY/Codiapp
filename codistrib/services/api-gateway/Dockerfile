# Build avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY services/api-gateway/pom.xml services/api-gateway/
RUN mvn -B -f services/api-gateway/pom.xml dependency:go-offline
COPY protos ./protos
COPY shared ./shared

# Télécharge les dépendances
RUN mvn -B -f services/api-gateway/pom.xml dependency:go-offline

COPY services/api-gateway/src services/api-gateway/src

# Build l'application (sans les tests)
RUN mvn -B -DskipTests -f services/api-gateway/pom.xml clean package

# Image runtime légère
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Installe curl pour le healthcheck
RUN apk add --no-cache curl

# Crée un utilisateur non-root
RUN addgroup -S codistrib && adduser -S codistrib -G codistrib

# Copie le JAR depuis l'étape de build
COPY --from=build /app/services/api-gateway/target/*.jar /app/app.jar

# Change le propriétaire
RUN chown codistrib:codistrib app.jar

# Utilise l'utilisateur non-root
USER codistrib

ARG API_GATEWAY_PORT
ARG AUTH_SERVICE_PORT

# Port exposé
EXPOSE ${API_GATEWAY_PORT}

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV AUTH_SERVICE_PORT=${AUTH_SERVICE_PORT}
ENV API_GATEWAY_PORT=${API_GATEWAY_PORT}

# Healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${API_GATEWAY_PORT}/auth/login || exit 1
# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]