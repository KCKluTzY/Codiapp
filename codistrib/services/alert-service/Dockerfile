# services/alert-service/Dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml /build/
# Copier aussi les pom parents utiles si le module hérite de gestion de versions
COPY services/alert-service/pom.xml /build/services/alert-service/pom.xml
# Copier le code du module (optimiser les couches si besoin)
COPY services/alert-service /build/services/alert-service
# Générer les stubs gRPC et packager
WORKDIR /build/services/alert-service
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
ENV JAVA_OPTS="-Xms256m -Xmx512m"
WORKDIR /app
COPY --from=build /build/services/alert-service/target/*.jar /app/app.jar
EXPOSE 9005
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]