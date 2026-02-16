# CODI'strib

**Système collaboratif d'aide à la mobilité pour les personnes avec déficience intellectuelle**

CODI'strib est une application distribuée permettant aux personnes avec déficience intellectuelle (DI) de recevoir de l'aide à distance d'aidants qualifiés lors de leurs déplacements.

---

## Table des matières

1. [Présentation](#présentation)
2. [Architecture](#architecture)
3. [Prérequis](#prérequis)
4. [Installation](#installation)
5. [Commandes utiles](#commandes-utiles)
6. [Structure du projet](#structure-du-projet)
7. [Contribuer](#contribuer)

---

## Présentation

### Contexte

Ce projet s'inscrit dans le cadre d'une recherche sur l'aide à la mobilité des personnes avec déficience intellectuelle. Il permet :

- L'accompagnement à distance lors des déplacements
- La gestion d'alertes et d'incidents
- La communication entre personnes DI et aidants via pictogrammes (ARA/SAAC)
- Le partage de position en temps réel

### Acteurs du système

| Rôle | Description |
|------|-------------|
| **Person DI** | Personne avec déficience intellectuelle, utilisateur principal |
| **Helper** | Aidant qualifié qui accompagne à distance |
| **Administrator** | Gestionnaire du système et des utilisateurs |

---

## Architecture

Le projet utilise une architecture **microservices** avec les technologies suivantes :

- **Backend** : Java 21, Spring Boot 3.2
- **Communication inter-services** : gRPC
- **Bases de données** : PostgreSQL, MongoDB, Redis
- **Conteneurisation** : Docker

### Services

| Service | Port (gRPC) | Base de données | Description |
|---------|-------------|-----------------|-------------|
| api-gateway | 8080 (REST) | Redis | Point d'entrée REST |
| auth-service | 9001 | PostgreSQL (auth_db) | Authentification JWT |
| user-service | 9002 | PostgreSQL (users_db) | Gestion des profils |
| messaging-service | 9003 | MongoDB (messaging_db) | Messages et pictogrammes |
| location-service | 9004 | MongoDB (location_db) | Géolocalisation |
| alert-service | 9005 | MongoDB (alerts_db) | Alertes et incidents |
| notification-service | 9006 | Redis | Notifications push |

---

## Prérequis

Avant de commencer, assure-toi d'avoir installé :

| Outil | Version minimale | Vérification |
|-------|------------------|--------------|
| Git | 2.x | `git --version` |
| Docker | 24.x | `docker --version` |
| Docker Compose | 2.x | `docker compose version` |
| Java | 21 | `java --version` |
| Maven | 3.9.x | `mvn --version` |

### Installation de Docker (Ubuntu)

```bash
# Mise à jour
sudo apt-get update

# Installation de Docker
sudo apt-get install docker.io

# Installation de Docker Compose
sudo apt-get install docker-compose-plugin

# Ajouter ton utilisateur au groupe docker (évite sudo)
sudo usermod -aG docker $USER

# Redémarrer la session pour appliquer les changements
```

---

## Installation

### 1. Cloner le projet

```bash
# Cloner le repository
git clone https://gitlab.com/votre-groupe/codistrib-backend.git

# Aller dans le dossier
cd codistrib-backend
```

### 2. Configurer l'environnement

```bash
# Copier le fichier d'exemple
cp .env.example .env

# (Optionnel) Modifier les mots de passe dans .env
nano .env
```

Le fichier `.env` contient les variables de configuration :

```env
# PostgreSQL
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_PORT=

# MongoDB
MONGO_USER=
MONGO_PASSWORD=
MONGO_PORT=

# Redis
REDIS_PASSWORD=
REDIS_PORT=

# JWT
JWT_SECRET=
```

### 3. Rendre les scripts exécutables

```bash
chmod +x dev.sh
chmod +x infrastructure/postgres/init-multiple-databases.sh
```

### 4. Démarrer l'infrastructure

```bash
# Démarrer les bases de données
./dev.sh start
```

Cette commande lance :
- **PostgreSQL** avec les bases `auth_db` et `users_db`
- **MongoDB** avec les bases `messaging_db`, `location_db` et `alerts_db`
- **Redis** pour le cache et les sessions

### 5. Vérifier l'installation

```bash
# Vérifier l'état des services
./dev.sh status
```

---

## Commandes utiles

Le script `dev.sh` fournit toutes les commandes nécessaires :

| Commande | Description |
|----------|-------------|
| `./dev.sh start` | Démarre l'infrastructure |
| `./dev.sh stop` | Arrête l'infrastructure |
| `./dev.sh restart` | Redémarre l'infrastructure |
| `./dev.sh status` | Affiche l'état des services |
| `./dev.sh logs` | Affiche les logs (Ctrl+C pour quitter) |
| `./dev.sh reset` | Supprime tout et recrée (perte de données) |
| `./dev.sh psql` | Ouvre une console PostgreSQL |
| `./dev.sh mongo` | Ouvre une console MongoDB |
| `./dev.sh redis` | Ouvre une console Redis |
| `./dev.sh env` | Affiche les variables d'environnement |

### Exemples

```bash
# Se connecter à PostgreSQL et lister les bases
./dev.sh psql
\l

# Se connecter à MongoDB et voir les collections
./dev.sh mongo
show dbs
use messaging_db
show collections

# Voir les logs en temps réel
./dev.sh logs
```

---

## Contribuer

### Branches

- `main` : Version stable
- `develop` : Développement en cours
- `feature/*` : Nouvelles fonctionnalités

### Workflow

```bash
# Créer une branche feature
git checkout -b feature/ma-fonctionnalite

# Faire des commits
git add .
git commit -m "feat: description de la fonctionnalité"

# Pousser et créer une merge request
git push origin feature/ma-fonctionnalite
```

---

## Licence

Projet académique - UPHF / LAMIH