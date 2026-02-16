# Documentation API REST - CODI'strib

Documentation complète de l'API REST exposée par l'**API Gateway** avec exemples de requêtes et réponses pour tous les endpoints.

---

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Configuration de base](#configuration-de-base)
3. [Authentification & JWT](#authentification--jwt)
4. [Routes d'authentification](#routes-dauthentification)
5. [Codes d'erreur](#codes-derreur)
6. [Rate Limiting](#rate-limiting)
7. [Exemples clients](#exemples-clients)
8. [Architecture interne (gRPC)](#architecture-interne-grpc)

---

## Vue d'ensemble

CODI'strib expose une **API REST** via l'**API Gateway**. L'API Gateway agit comme un proxy qui :

- 🔐 Valide les tokens JWT
- 🎯 Routage vers les microservices gRPC
- ⚡ Applique le rate limiting par IP/utilisateur
- 📊 Enregistre les requêtes
- 🔄 Convertit JSON ↔ Protocol Buffers (gRPC)

### Caractéristiques

- ✅ **Routes publiques** : Authentification sans JWT
- 🔐 **Routes authentifiées** : Requièrent un token JWT
- 👥 **Contrôle d'accès par rôle** : PERSON_DI, HELPER, ADMINISTRATOR
- 📍 **URL de base** : `http://localhost:8080/api/v1`
- 🔄 **Résilience** : Retry & Circuit Breaker intégrés

---

## Configuration de base

### Port

Le port est configurable via la variable d'environnement `API_GATEWAY_PORT` (défaut: **8080**).

```bash
# Depuis .env
API_GATEWAY_PORT=8080
```

### Headers HTTP standard

Tous les endpoints retournent des headers standard:

```
Content-Type: application/json
Access-Control-Allow-Origin: *
X-Request-ID: uuid (pour traçabilité)
```

### Timeouts

- Timeout de requête gRPC: 30 secondes
- Timeout de connexion: 10 secondes

---

## Authentification & JWT

### Format du token JWT

```
Authorization: Bearer <JWT>
```

Les tokens JWT contiennent les claims suivants:

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",  // user_id
  "username": "alice_dupont",
  "email": "alice@example.com",
  "role": "ROLE_HELPER",
  "iat": 1707216000,                              // issued at
  "exp": 1707219600                               // expiration
}
```

### Stockage du token (recommandations)

```javascript
// ✅ À FAIRE
sessionStorage.setItem('accessToken', token);    // Session storage
localStorage.setItem('refreshToken', token);     // Refresh token seulement

// ❌ À ÉVITER
localStorage.setItem('accessToken', token);      // XSS vulnerability
document.cookie = token;                         // CSRF risk
```

### Utilisation du token

```bash
# Requête authentifiée
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8080/protected-route
```

---

## Routes d'authentification

### 1. POST `/api/v1/auth/register`

**Description :** Inscription d'un nouvel utilisateur.

**Authentification requise :** ❌ Non

**Requête :**
```json
{
  "username": "alice_dupont",
  "email": "alice@example.com",
  "password": "SecurePassword123!",
  "role": "ROLE_HELPER"
}
```

**Validations :**
- `username` : 3-50 caractères, unique
- `email` : Format email valide, unique
- `password` : Min 8 caractères
- `role` : ROLE_PERSON_DI | ROLE_HELPER | ROLE_ADMINISTRATOR

**Réponse (201 Created) :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "role": "ROLE_HELPER"
}
```

**Erreurs possibles :**
- `400 Bad Request` - Données invalides
- `409 Conflict` - Email/username déjà utilisé
- `422 Unprocessable Entity` - Validation échouée

**Exemple cURL :**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_dupont",
    "email": "alice@example.com",
    "password": "SecurePassword123!",
    "role": "ROLE_HELPER"
  }'
```

---

### 2. POST `/api/v1/auth/login`

**Description :** Authentification d'un utilisateur existant.

**Authentification requise :** ❌ Non

**Requête :**
```json
{
  "identifier": "alice@example.com",
  "password": "SecurePassword123!"
}
```

**Paramètres :**
- `identifier` : Email OU username
- `password` : Mot de passe en clair

**Réponse (200 OK) :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "role": "ROLE_HELPER"
}
```

**Erreurs possibles :**
- `400 Bad Request` - Données invalides
- `401 Unauthorized` - Identifiants incorrects
- `404 Not Found` - Utilisateur inexistant

**Exemple cURL :**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "alice@example.com",
    "password": "SecurePassword123!"
  }'
```

**Exemple JavaScript :**
```javascript
async function login(identifier, password) {
  const response = await fetch('http://localhost:8080/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ identifier, password })
  });

  if (!response.ok) throw new Error('Login failed');
  
  const data = await response.json();
  sessionStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  
  return data;
}
```

---

### 3. POST `/api/v1/auth/refresh`

**Description :** Régénère un access token à partir du refresh token. **Effectue une rotation du refresh token.**

**Authentification requise :** ❌ Non (mais requiert un refresh token valide)

**Requête :**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Réponse (200 OK) :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "role": "ROLE_HELPER"
}
```

**Erreurs possibles :**
- `400 Bad Request` - Format token incorrect
- `401 Unauthorized` - Refresh token expiré/invalide

**Exemple cURL :**
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

**Exemple JavaScript :**
```javascript
async function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('http://localhost:8080/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });

  if (!response.ok) {
    // Refresh token expiré -> rediriger vers login
    window.location.href = '/login';
    throw new Error('Refresh token expired');
  }

  const data = await response.json();
  sessionStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  
  return data.accessToken;
}
```

---

### 4. POST `/api/v1/auth/logout`

**Description :** Déconnexion - révoque les tokens en les ajoutant à la blacklist Redis.

**Authentification requise :** ✅ Oui (Authorization header)

**Requête :**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Réponse (200 OK) :**
```json
{
  "success": true,
  "message": "Déconnexion réussie"
}
```

**Erreurs possibles :**
- `400 Bad Request` - Tokens malformés
- `401 Unauthorized` - Tokens invalides/expirés
- `503 Service Unavailable` - Redis indisponible

**Exemple cURL :**
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

**Exemple JavaScript :**
```javascript
async function logout(accessToken, refreshToken) {
  const response = await fetch('http://localhost:8080/auth/logout', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`
    },
    body: JSON.stringify({ accessToken, refreshToken })
  });

  if (!response.ok) throw new Error('Logout failed');

  // Nettoyer le storage local
  sessionStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');

  // Rediriger vers login
  window.location.href = '/login';
}
```

---

## Routes protégées (par rôle)

### Matrice d'accès

```yaml
Routes publiques (sans JWT):
  - POST   /api/v1/auth/register
  - POST   /api/v1/auth/login
  - POST   /api/v1/auth/refresh
  - GET    /actuator/health

Routes PERSON_DI:
  - POST   /api/v1/alerts/create
  - POST   /api/v1/locations/share
  - GET    /api/v1/helpers/available

Routes HELPER:
  - POST   /api/v1/alerts/{alertId}/assign
  - POST   /api/v1/alerts/{alertId}/resolve
  - POST   /api/v1/helpers/availability

Routes ADMINISTRATOR:
  - GET    /api/v1/admin/**
  - GET    /api/v1/users/all
  - PUT    /api/v1/users/{userId}/role
```

### Vérification des permissions

L'API Gateway vérifie automatiquement les rôles:

1. **Extraction du token** depuis le header `Authorization`
2. **Validation du JWT** (signature, expiration)
3. **Vérification de la blacklist** (Redis) - token révoqué?
4. **Vérification du rôle** - l'utilisateur peut-il accéder à cette route?
5. **Routage** vers le microservice approprié

Si le JWT est manquant ou invalide:

```json
{
  "timestamp": "2026-02-06T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Missing or invalid JWT token",
  "path": "/protected-route"
}
```

---

## Codes d'erreur

### HTTP Status Codes

| Code | Signification | Exemple |
|------|--------------|---------|
| **200** | OK | Login réussi |
| **201** | Created | Register réussi |
| **400** | Bad Request | Données invalides |
| **401** | Unauthorized | JWT invalide/expiré |
| **403** | Forbidden | Rôle insuffisant |
| **404** | Not Found | Ressource inexistante |
| **409** | Conflict | Email/username déjà utilisé |
| **422** | Unprocessable Entity | Validation échouée |
| **429** | Too Many Requests | Rate limit dépassé |
| **500** | Internal Server Error | Erreur serveur |
| **503** | Service Unavailable | Microservice down |

### Format d'erreur standard

```json
{
  "timestamp": "2026-02-06T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: email must be valid",
  "path": "/auth/register"
}
```

### Erreurs spécifiques

**Email déjà existant:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email 'alice@example.com' is already in use"
}
```

**Token expiré:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token has expired"
}
```

**Rate limit dépassé:**
```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded: 10 requests/second",
  "retryAfter": 5
}
```

---

## Rate Limiting

### Configuration

```yaml
rate-limit:
  enabled: true
  default:
    requests-per-second: 10
    burst-capacity: 200
```

### Headers de rate limiting

```
X-RateLimit-Limit: 10
X-RateLimit-Remaining: 9
X-RateLimit-Reset: 1707216001
```

### Comportement

- **Limite par défaut** : 10 requêtes/seconde par IP
- **Burst** : Jusqu'à 200 requêtes peuvent être accumulées
- **Reset** : La limite se réinitialise chaque seconde

**Exemple de dépassement:**

```bash
# Faire 11 requêtes en 1 seconde
for i in {1..11}; do
  curl http://localhost:8080/api/v1/auth/login ...
done

# La 11ème requête retourne:
# HTTP 429 Too Many Requests
# X-RateLimit-Remaining: 0
# Retry-After: 1
```

---

## Architecture interne (gRPC)

### Flux de requête

```
Client HTTP
    ↓
API Gateway (Port 8080)
    ↓
JwtAuthFilter (validation JWT)
    ↓
RateLimitFilter (limitation de débit)
    ↓
LoggingFilter (enregistrement)
    ↓
AuthController (REST)
    ↓
AuthServiceGrpcClient
    ↓
Auth Service (gRPC, Port 9001)
    ↓
PostgreSQL / Redis
    ↓
Response (TokenResponseDto → TokenResponse proto)
    ↓
Client HTTP
```

### Services gRPC internes

L'API Gateway communique avec ces services via gRPC:

| Service | Port | Rôle |
|---------|------|------|
| **auth-service** | 9001 | Authentification, gestion tokens JWT |
| **user-service** | 9002 | Profils utilisateurs, permissions |
| **messaging-service** | 9003 | Messagerie et notifications |
| **location-service** | 9004 | Géolocalisation |
| **alert-service** | 9005 | Gestion des alertes |
| **notification-service** | 9006 | Notifications |

### Configuration gRPC (depuis application.yml)

```yaml
grpc:
  client:
    auth-service:
      host: localhost
      port: ${AUTH_SERVICE_PORT}
    user-service:
      host: localhost
      port: ${USER_SERVICE_PORT}
    # ... autres services
```

Chaque port est configurable via `.env` pour permettre le déploiement en conteneurs.

---

## Bonnes pratiques

### 1. Sécurité

✅ **À FAIRE :**
- Valider TOUS les tokens JWT reçus
- Utiliser HTTPS en production
- Implémenter le CORS strictement
- Hashage des mots de passe (bcrypt/argon2)
- Rotation des refresh tokens

❌ **À ÉVITER :**
- Mettre les tokens en paramètres GET
- Stocker les tokens en localStorage (utiliser sessionStorage)
- Envoyer les mots de passe en clair
- Ignorer les erreurs de validation
- Accepter les CORS de n'importe quelle origine

### 2. Gestion du JWT

```javascript
// Intercepteur pour appels API
function makeAuthenticatedRequest(endpoint, method = 'GET', body = null) {
  const token = sessionStorage.getItem('accessToken');
  
  const options = {
    method,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  };
  
  if (body) options.body = JSON.stringify(body);
  
  return fetch(endpoint, options)
    .then(response => {
      if (response.status === 401) {
        // Token expiré -> rafraîchir
        return refreshAndRetry(endpoint, options);
      }
      return response;
    });
}
```

### 3. Gestion des erreurs

```javascript
async function handleApiError(error) {
  if (error.response?.status === 401) {
    // Token invalide/expiré -> rediriger vers login
    sessionStorage.removeItem('accessToken');
    window.location.href = '/login';
  } else if (error.response?.status === 403) {
    // Rôle insuffisant
    console.error('Accès refusé - rôle insuffisant');
  } else if (error.response?.status === 429) {
    // Rate limit dépassé
    const retryAfter = error.response.headers['retry-after'];
    console.warn(`Trop de requêtes. Réessai dans ${retryAfter}s`);
  } else {
    // Erreur serveur
    console.error('Erreur serveur:', error);
  }
}
```

### 4. Performance

- Réutiliser les connexions HTTP (HTTP/2, Keep-Alive)
- Implémenter un cache client pour les données stables
- Utiliser les timeouts appropriés
- Limiter les appels API (debounce, throttle)

### 5. Logging & Monitoring

```javascript
// Logger toutes les requêtes/réponses
api.interceptors.response.use(
  (response) => {
    console.log(`[${response.status}] ${response.config.method.toUpperCase()} ${response.config.url}`);
    return response;
  },
  (error) => {
    console.error(`[${error.response?.status}] ${error.config.method.toUpperCase()} ${error.config.url}`);
    return Promise.reject(error);
  }
);
```

---

## Ressources

- [RFC 7519 - JSON Web Token (JWT)](https://tools.ietf.org/html/rfc7519)
- [OWASP - Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [REST API Best Practices](https://restfulapi.net/)
- [gRPC Documentation](https://grpc.io/)

---

## Support & Feedback

Pour questions ou améliorations: consultez la documentation du projet CODI'strib ou contactez l'équipe de développement.

**Dernière mise à jour:** 6 février 2026
