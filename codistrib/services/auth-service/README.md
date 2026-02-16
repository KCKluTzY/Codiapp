# Auth Service (Microservice d'authentification)

Service d'authentification basé sur Spring Boot 3, gRPC et JWT. Il gère:
- **Register** : inscription + émission d'un couple de tokens (access JWT + refresh token)
- **Login** : authentification + émission de nouveaux tokens
- **RefreshToken** : régénère un access token (avec rotation du refresh token)
- **ValidateToken** : vérifie signature/expiration/blacklist d'un access token
- **Logout** : ajoute le `jti` du token à la blacklist (Redis) et révoque le refresh token

**Technos :**
- Java 21, Spring Boot 3.2.x, Spring Data JPA (PostgreSQL), Spring Data Redis
- JWT via io.jsonwebtoken (JJWT)
- gRPC via net.devh grpc-server-spring-boot-starter

---

## Prérequis
- Java 21
- Maven 3.9+
- Docker (recommandé)
- PostgreSQL et Redis accessibles (voir configuration ci-dessous)

---

## Configuration

Le service utilise des variables d'environnement définies dans le fichier `.env` à la racine du projet. Voir [.env.example](../../.env.example) pour le modèle.

### Variables d'environnement requises:

**Base de données (PostgreSQL)**
- `POSTGRES_HOST` - Hôte PostgreSQL
- `POSTGRES_PORT` - Port PostgreSQL
- `POSTGRES_USER` - Utilisateur PostgreSQL
- `POSTGRES_PASSWORD` - Mot de passe PostgreSQL
- `POSTGRES_AUTH_SERVICE_DB` - Nom de la base de données pour auth-service

**Redis (blacklist des tokens)**
- `REDIS_HOST` - Hôte Redis
- `REDIS_PORT` - Port Redis
- `REDIS_PASSWORD` - Mot de passe Redis (optionnel)

**JWT**
- `JWT_SECRET` - Clé secrète pour la signature JWT (>= 256 bits)
- `JWT_ACCESS_TOKEN_EXPIRATION` - Durée de vie du token d'accès en secondes
- `JWT_REFRESH_TOKEN_EXPIRATION` - Durée de vie du refresh token en secondes

**Services**
- `AUTH_SERVICE_PORT` - Port du service gRPC (défaut: 9001)

### Fichier `src/main/resources/application.yml`:
```yaml
server:
  port: ${AUTH_SERVICE_PORT}
grpc:
  server:
    port: ${AUTH_SERVICE_PORT}
spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_AUTH_SERVICE_DB}
    username: ${POSTGRES_USER}
    password: ${POSTGRES_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
  data:
    redis:
      repositories:
        enabled: false
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
jwt:
  secret: ${JWT_SECRET}
  issuer: codistrib-auth
  access-token-validity-seconds: ${JWT_ACCESS_TOKEN_EXPIRATION}
  refresh-token-validity-seconds: ${JWT_REFRESH_TOKEN_EXPIRATION}
management:
  endpoints:
    web:
      exposure:
        include: health,info
```

---

## Construire et lancer

### En local (avec Maven)

**Prérequis:**
1. Configurer les variables d'environnement du `.env` (PostgreSQL, Redis, JWT, etc.)
2. S'assurer que PostgreSQL et Redis sont en cours d'exécution

**Lancer l'application :**
```bash
# Charger les variables d'environnement du .env
source .env

# Démarrer le service
mvn -q -f services/auth-service/pom.xml spring-boot:run
```

**Vérifier l'état du service (Healthcheck):**
```bash
curl http://localhost:9001/actuator/health
```

### Avec Docker

**Construire l'image:**
```bash
docker build -f services/auth-service/Dockerfile -t codistrib/auth-service:dev .
```

**Exécuter avec Docker Compose** (recommandé):

Voir le fichier `docker-compose.dev.yml` à la racine du projet. Le service est configuré pour dépendre de PostgreSQL et Redis avec les variables d'environnement appropriées.

```bash
docker-compose -f docker-compose.dev.yml up auth-service
```

---

## API gRPC

### Exemples d'appels avec grpcurl

**Register (Inscription):**
```bash
grpcurl -plaintext localhost:9001 com.codistrib.proto.auth.AuthService/Register \
  -d '{"username":"alice","email":"alice@example.com","password":"SecurePassword123!","role":"ROLE_HELPER"}'
```

**Login (Connexion):**
```bash
grpcurl -plaintext localhost:9001 com.codistrib.proto.auth.AuthService/Login \
  -d '{"identifier":"alice@example.com","password":"SecurePassword123!"}'
```

**RefreshToken (Régénérer le token d'accès):**
```bash
grpcurl -plaintext localhost:9001 com.codistrib.proto.auth.AuthService/RefreshToken \
  -d '{"refresh_token":"<REFRESH_TOKEN>"}'
```

**ValidateToken (Valider un token):**
```bash
grpcurl -plaintext localhost:9001 com.codistrib.proto.auth.AuthService/ValidateToken \
  -d '{"access_token":"<ACCESS_TOKEN>"}'
```

**Logout (Déconnexion):**
```bash
grpcurl -plaintext localhost:9001 com.codistrib.proto.auth.AuthService/Logout \
  -d '{"access_token":"<ACCESS_TOKEN>","refresh_token":"<REFRESH_TOKEN>"}'
```

> **Note :** Pour `ValidateToken`, passer uniquement le token JWT brut (sans préfixe "Bearer").

---

## Tests

Exécuter les tests unitaires :
```bash
mvn -q -f services/auth-service/pom.xml test
```

---

## Dépannage

**ValidateToken renvoie `is_valid=false` :**
- Assurez-vous de passer l'access token (JWT) et pas le refresh token
- Ne pas inclure le préfixe "Bearer "
- Vérifiez que `JWT_SECRET` est identique pour l'émission et la validation
- Si Redis est indisponible, la validation peut échouer selon la stratégie. Le service ignore les erreurs Redis (fail-open) pour éviter les faux négatifs

**Logout échoue avec erreur Redis :**
- Vérifiez `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`
- En Docker Compose, utilisez `redis` comme host; en local, utiliser la valeur de `REDIS_HOST` du `.env`

**Port gRPC :**
- gRPC écoute sur le port défini dans `AUTH_SERVICE_PORT` (défaut : 9001)
- Assurez-vous d'appeler le bon port et que le service n'est pas bloqué par un firewall

---

## Améliorations possibles

- Mapper les exceptions métier vers les statuts gRPC (`UNAUTHENTICATED`, `ALREADY_EXISTS`, etc.)
- Ajouter Flyway pour versionner le schéma de la base de données
- Ajouter des tests d'intégration avec Testcontainers (PostgreSQL + Redis)
- Rotation stricte des refresh tokens
- Politique de mot de passe renforcée avec i18n
- Observabilité : Actuator metrics et logging de sécurité

---

## Licence
Usage interne CODI'strib.
