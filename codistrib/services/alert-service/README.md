# alert-service

Service de gestion des alertes (SOS) du système CODI'strib. Il centralise le cycle de vie d’une alerte initiée par une personne avec DI et prise en charge par un aidant (Helper) ou un responsable, en s’appuyant sur MongoDB pour la persistance et gRPC pour la communication inter‑services.

---

## Sommaire
- [Rôle et périmètre](#rôle-et-périmètre)
- [Architecture et communications](#architecture-et-communications)
- [Modèle de données](#modèle-de-données)
- [Contrat gRPC (API interne)](#contrat-grpc-api-interne)
- [Routes exposées via l’API Gateway (REST)](#routes-exposées-via-lapi-gateway-rest)
- [Démarrage et exécution](#démarrage-et-exécution)
  - [Prérequis](#prérequis)
  - [Configuration (variables d’environnement)](#configuration-variables-denvironnement)
  - [Lancer en mode développement (recommandé)](#lancer-en-mode-développement-recommandé)
  - [Lancer en Docker (mode intégration)](#lancer-en-docker-mode-intégration)
- [Exemples d’utilisation](#exemples-dutilisation)
  - [Appels directs gRPC (grpcurl)](#appels-directs-grpc-grpcurl)
  - [Appels REST via l’API Gateway (curl)](#appels-rest-via-lapi-gateway-curl)
- [Observabilité et santé](#observabilité-et-santé)
- [Dépannage (FAQ)](#dépannage-faq)
- [Contribuer](#contribuer)

---

## Rôle et périmètre
- Créer une alerte (SOS) lorsqu’une Person DI a besoin d’aide
- Gérer les transitions d’état d’une alerte:
  - OPEN → ASSIGNED → RESOLVED (et éventuellement CANCELED)
- Enregistrer la personne déclencheuse, l’aidant assigné, les horodatages
- Offrir des opérations de lecture pour le détail et les listes filtrées

Ce service ne gère pas:
- L’authentification JWT (déléguée à auth-service + API Gateway)
- Les identités/relations détaillées (user-service)
- Le suivi GPS continu (location-service)
- La messagerie en temps réel (messaging-service)
- L’envoi de notifications (notification-service)

---

## Architecture et communications
- Langage/Framework: Java 21, Spring Boot 3.2
- Persistance: MongoDB (base: `alerts_db`)
- Interface inter‑services: gRPC (serveur gRPC embarqué)
- Exposition REST: indirecte, via l’API Gateway qui appelle gRPC

Flux typique:
1. Un client (web/mobile) appelle l’API Gateway REST `/alerts/create` avec un JWT valide
2. L’API Gateway valide le JWT et les rôles, puis appelle `alert-service` en gRPC
3. `alert-service` applique les règles métier et persiste l’alerte dans MongoDB
4. La réponse gRPC est convertie par la Gateway en JSON HTTP

Ports par défaut (surchageables via `.env`):
- alert-service (gRPC/HTTP management): `9005` (ALERT_SERVICE_PORT)
- api-gateway (REST): `8080` (API_GATEWAY_PORT)

---

## Modèle de données
Document MongoDB `alerts` (classe `domain.model.Alert`):
- id: String (ObjectId sous‑jacent)
- personId: String (ID de la personne DI)
- helperId: String? (ID de l’aidant assigné)
- type: String (ex.: SOS, INCIDENT)
- message: String (court descriptif)
- lat, lon: Double? (facultatifs)
- status: enum `AlertStatus` { OPEN, ASSIGNED, RESOLVED, CANCELED }
- createdAt, updatedAt, resolvedAt: Instant

Requêtes courantes (repository Mongo):
- `findByPersonId(String personId)`
- `findByHelperId(String helperId)`
- `findByStatus(AlertStatus status)`

---

## Contrat gRPC (API interne)
Paquet: `com.codistrib.proto.alert` (généré depuis `protos/alert.proto`).

Méthodes principales:
- `CreateAlert(CreateAlertRequest) → CreateAlertResponse`
- `AssignAlert(AssignAlertRequest) → AssignAlertResponse`
- `ResolveAlert(ResolveAlertRequest) → ResolveAlertResponse`
- `GetAlert(GetAlertRequest) → GetAlertResponse`
- `ListAlertsByPerson(ListByPersonRequest) → ListAlertsResponse`
- `ListAlertsByStatus(ListByStatusRequest) → ListAlertsResponse`

Gestion d’erreurs (mapping exceptions → statuts gRPC):
- `IllegalArgumentException` → `INVALID_ARGUMENT`
- « not found » → `NOT_FOUND`
- Violations de règles d’état (ex.: "Only OPEN can be ASSIGNED") → `FAILED_PRECONDITION`
- Par défaut → `INTERNAL`

---

## Routes exposées via l’API Gateway (REST)
L’API Gateway expose des routes REST qui appellent ce service en gRPC. Rôles (extraits d’API.md):

- PERSON_DI:
  - `POST /alerts/create`
- HELPER:
  - `POST /alerts/{alertId}/assign`
  - `POST /alerts/{alertId}/resolve`

Routes de lecture REST (recommandées pour la Gateway; selon votre implémentation actuelle de la Gateway, elles peuvent être ajoutées facilement):
- `GET /alerts/{id}` → `GetAlert`
- `GET /alerts?personId={pid}` → `ListAlertsByPerson`
- `GET /alerts?status={OPEN|ASSIGNED|RESOLVED|CANCELED}` → `ListAlertsByStatus`

Notes:
- La Gateway extrait `userId` et `role` du JWT pour contrôler l’accès et, au besoin, injecter `personId`/`helperId` dans les requêtes gRPC.

---

## Démarrage et exécution

### Prérequis
- Java 21, Maven 3.9+
- Docker / Docker Compose (MongoDB)
- `grpcurl` pour tester rapidement les endpoints gRPC (optionnel mais pratique)

### Configuration (variables d’environnement)
Les variables sont lues depuis l’environnement et/ou `.env`. Des valeurs par défaut sûres existent dans `application.yml` pour le dev local.

Essentielles:
- `ALERT_SERVICE_PORT` (défaut: 9005)
- `MONGO_HOST` (défaut: localhost en dev)
- `MONGO_PORT` (défaut: 27017)
- `MONGO_USER` (défaut: codistrib)
- `MONGO_PASSWORD` (défaut: codistrib123)

Fichiers de configuration:
- `src/main/resources/application.yml` (dev/local)
- `src/main/resources/application-docker.yml` (profil `docker`, host Mongo = `mongo`)

### Lancer en mode développement (recommandé)
1) Démarrer MongoDB (via Compose):
```powershell
docker compose -f docker-compose.dev.yml up -d mongo
```
2) Générer les stubs gRPC (si vous avez modifié `protos/alert.proto`):
```powershell
mvn -q -f services/alert-service/pom.xml generate-sources
```
3) Démarrer le service (profil dev implicite):
```powershell
mvn -q -f services/alert-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev
```

### Lancer en Docker (mode intégration)
- Profil actif: `docker` (Mongo host = `mongo`)
- Exemple de service à ajouter dans `docker-compose.dev.yml`:
```yaml
alert-service:
  build:
    context: .
    dockerfile: services/alert-service/Dockerfile
  container_name: codistrib-alert-service
  environment:
    SPRING_PROFILES_ACTIVE: docker
    MONGO_USER: ${MONGO_USER}
    MONGO_PASSWORD: ${MONGO_PASSWORD}
    MONGO_PORT: ${MONGO_PORT}
    ALERT_SERVICE_PORT: ${ALERT_SERVICE_PORT}
  ports:
    - "${ALERT_SERVICE_PORT}:${ALERT_SERVICE_PORT}"
  depends_on:
    mongo:
      condition: service_healthy
  networks:
    - codistrib-network
  restart: unless-stopped
```

---

## Exemples d’utilisation

### Appels directs gRPC (grpcurl)
Lister les services disponibles:
```powershell
grpcurl -plaintext localhost:9005 list
```
Créer une alerte:
```powershell
grpcurl -plaintext -d "{\"personId\":\"u1\",\"type\":\"SOS\",\"message\":\"Help\"}" \
  localhost:9005 com.codistrib.proto.alert.AlertService/CreateAlert
```
Assigner une alerte (remplacez <ID>):
```powershell
grpcurl -plaintext -d "{\"alertId\":\"<ID>\",\"helperId\":\"h1\"}" \
  localhost:9005 com.codistrib.proto.alert.AlertService/AssignAlert
```
Résoudre une alerte:
```powershell
grpcurl -plaintext -d "{\"alertId\":\"<ID>\",\"helperId\":\"h1\"}" \
  localhost:9005 com.codistrib.proto.alert.AlertService/ResolveAlert
```
Lecture:
```powershell
# Get
grpcurl -plaintext -d "{\"alertId\":\"<ID>\"}" \
  localhost:9005 com.codistrib.proto.alert.AlertService/GetAlert
# List by person
grpcurl -plaintext -d "{\"personId\":\"u1\"}" \
  localhost:9005 com.codistrib.proto.alert.AlertService/ListAlertsByPerson
# List by status
grpcurl -plaintext -d "{\"status\":\"OPEN\"}" \
  localhost:9005 com.codistrib.proto.alert.AlertService/ListAlertsByStatus
```

### Appels REST via l’API Gateway (curl)
Pré-requis: un `accessToken` JWT valide dans `Authorization: Bearer <token>`.

Créer une alerte (PERSON_DI):
```bash
curl -X POST http://localhost:8080/alerts/create \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "SOS",
    "message": "Help please",
    "lat": 50.63,
    "lon": 3.06
  }'
```
Assigner (HELPER):
```bash
curl -X POST http://localhost:8080/alerts/<ID>/assign \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "helperId": "h1" }'
```
Résoudre (HELPER):
```bash
curl -X POST http://localhost:8080/alerts/<ID>/resolve \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "helperId": "h1" }'
```
Lecture (si ajoutées côté Gateway):
```bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" http://localhost:8080/alerts/<ID>
curl -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/alerts?personId=u1"
curl -H "Authorization: Bearer $ACCESS_TOKEN" "http://localhost:8080/alerts?status=OPEN"
```

---

## Observabilité et santé
- Actuator (HTTP management): `GET /actuator/health` exposé localement par Spring Boot (inclus via `management.endpoints.web.exposure.include=health,info`).
- Logs: config par défaut INFO, possibilité de passer `com.codistrib.alertservice` en DEBUG en dev.

---

## Dépannage (FAQ)
- « Could not resolve placeholder 'ALERT_SERVICE_PORT' »
  - Définir les variables dans l’environnement ou s’appuyer sur les valeurs par défaut déjà présentes dans `application.yml`.
- Tentative de configuration d’une DataSource JDBC/Hikari
  - Vérifiez que le starter JPA n’est pas dans le `pom.xml` de `alert-service` (ce service utilise MongoDB uniquement).
- `UNAVAILABLE: io exception` avec `grpcurl`
  - Le service n’écoute pas (vérifiez qu’il est démarré) ou port bloqué par un firewall; contrôlez avec `netstat`.
- Connexion Mongo en Docker
  - Utilisez le profil `docker` (env `SPRING_PROFILES_ACTIVE=docker`) afin que l’hôte soit `mongo` et non `localhost`.

---

## Contribuer
- Standard de code: Java 21, Spring Boot 3, conventions des autres services du monorepo
- Protos: centralisés dans le dossier racine `protos/`
- Tests: 
  - Unitaires pour les règles métier (transitions d’état)
  - Intégration pour le repository Mongo (Testcontainers recommandé)
  - Tests d’API gRPC avec `grpcurl` ou stubs clients

Dernière mise à jour: 10 février 2026
