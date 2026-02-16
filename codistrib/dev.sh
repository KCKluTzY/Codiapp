#!/bin/bash
# CODI'strib - Script de gestion infrastructure
#
# Usage : ./dev.sh [commande]
#
# Commandes disponibles :
#   start     Démarre l'infrastructure
#   stop      Arrête l'infrastructure
#   restart   Redémarre l'infrastructure
#   status    Affiche l'état des conteneurs
#   logs      Affiche les logs (Ctrl+C pour quitter)
#   reset     Supprime tout et recrée (perte de données)
#   psql      Ouvre une console PostgreSQL
#   mongo     Ouvre une console MongoDB
#   redis     Ouvre une console Redis
#   help      Affiche cette aide
#
# ============================================

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

COMPOSE_FILE="docker-compose.dev.yml"

set -a

# Détecter la commande docker compose disponible
# Docker Compose V2 utilise "docker compose"
# Docker Compose V1 utilise "docker-compose"
detect_compose_command() {
    if docker compose version > /dev/null 2>&1; then
        COMPOSE_CMD="docker compose"
    elif docker-compose --version > /dev/null 2>&1; then
        COMPOSE_CMD="docker-compose"
    else
        echo -e "${RED} Docker Compose n'est pas installé !${NC}"
        echo ""
        echo "Installe Docker Compose pour continuer."
        exit 1
    fi
}

show_logo() {
    echo -e "${BLUE}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║                                                           ║"
    echo "║      ██████╗ ██████╗ ██████╗ ██╗███████╗████████╗██████╗  ║"
    echo "║     ██╔════╝██╔═══██╗██╔══██╗██║██╔════╝╚══██╔══╝██╔══██╗ ║"
    echo "║     ██║     ██║   ██║██║  ██║██║███████╗   ██║   ██████╔╝ ║"
    echo "║     ██║     ██║   ██║██║  ██║██║╚════██║   ██║   ██╔══██╗ ║"
    echo "║     ╚██████╗╚██████╔╝██████╔╝██║███████║   ██║   ██║  ██║ ║"
    echo "║      ╚═════╝ ╚═════╝ ╚═════╝ ╚═╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ║"
    echo "║                                                           ║"
    echo "║              Infrastructure de développement              ║"
    echo "║                                                           ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Fonction pour vérifier si Docker est lancé
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED} Docker n'est pas lancé !${NC}"
        echo "   Démarre Docker Desktop ou le service Docker."
        exit 1
    fi
}

# Fonction pour vérifier si le fichier .env existe
check_env() {
    if [ ! -f ".env" ]; then
        echo -e "${YELLOW} Fichier .env non trouvé.${NC}"
        echo "   Création à partir de .env.example..."
        if [ -f ".env.example" ]; then
            cp .env.example .env
            echo -e "${GREEN}   Fichier .env créé !${NC}"
            echo -e "${YELLOW}    Pense à modifier les mots de passe dans .env${NC}"
        else
            echo -e "${RED}   Fichier .env.example non trouvé !${NC}"
            exit 1
        fi
    fi
}

# Fonction : Démarrer l'infrastructure
cmd_start() {
    echo -e "${GREEN} Démarrage de l'infrastructure...${NC}"
    echo ""
    $COMPOSE_CMD -f "$COMPOSE_FILE" up -d
    echo ""
    echo -e "${GREEN} Infrastructure démarrée !${NC}"
    echo ""
    cmd_status
}

# Fonction : Arrêter l'infrastructure
cmd_stop() {
    echo -e "${YELLOW} Arrêt de l'infrastructure...${NC}"
    $COMPOSE_CMD -f $COMPOSE_FILE down
    echo -e "${GREEN} Infrastructure arrêtée.${NC}"
}

# Fonction : Redémarrer l'infrastructure
cmd_restart() {
    echo -e "${YELLOW} Redémarrage de l'infrastructure...${NC}"
    $COMPOSE_CMD -f $COMPOSE_FILE restart
    echo -e "${GREEN} Infrastructure redémarrée.${NC}"
    cmd_status
}

# Fonction : Afficher le statut
cmd_status() {
    echo -e "${BLUE} État des conteneurs :${NC}"
    echo ""
    $COMPOSE_CMD -f $COMPOSE_FILE ps
    echo ""
    
    # Vérification détaillée
    echo -e "${BLUE} Vérification des services :${NC}"
    echo ""
    
    # PostgreSQL
    if docker exec codistrib-postgres pg_isready -U codistrib > /dev/null 2>&1; then
        echo -e "   PostgreSQL : ${GREEN} Opérationnel${NC}"
    else
        echo -e "   PostgreSQL : ${RED} Non disponible${NC}"
    fi
    
    # MongoDB
    if docker exec codistrib-mongo mongosh --quiet --eval "db.runCommand('ping').ok" > /dev/null 2>&1; then
        echo -e "   MongoDB    : ${GREEN} Opérationnel${NC}"
    else
        echo -e "   MongoDB    : ${RED} Non disponible${NC}"
    fi
    
    # Redis
    if docker exec codistrib-redis redis-cli -a ${REDIS_PASSWORD} ping > /dev/null 2>&1; then
        echo -e "   Redis      : ${GREEN} Opérationnel${NC}"
    else
        echo -e "   Redis      : ${RED} Non disponible${NC}"
    fi
    
    # Auth Service (gRPC uniquement, pas de serveur HTTP)
    if docker exec codistrib-auth-service nc -z localhost ${AUTH_SERVICE_PORT} > /dev/null 2>&1; then
        echo -e "   Auth Service : ${GREEN} Opérationnel${NC}"
    else
        echo -e "   Auth Service : ${RED} Non disponible${NC}"
    fi

    # User Service (gRPC uniquement, pas de serveur HTTP)
    if docker exec codistrib-user-service nc -z localhost ${USER_SERVICE_PORT} > /dev/null 2>&1; then
        echo -e "   User Service : ${GREEN} Opérationnel${NC}"
    else
        echo -e "   User Service : ${RED} Non disponible${NC}"
    fi
    
    # API Gateway
    if curl -s http://localhost:${API_GATEWAY_PORT}/actuator/health | grep -q '"status":"UP"'; then
        echo -e "   API Gateway  : ${GREEN} Opérationnel${NC}"
    else
        echo -e "   API Gateway  : ${RED} Non disponible${NC}"
    fi
    
    echo ""
}

# Fonction : Afficher les logs
cmd_logs() {
    echo -e "${BLUE} Logs de l'infrastructure (Ctrl+C pour quitter)${NC}"
    echo ""
    $COMPOSE_CMD -f $COMPOSE_FILE logs -f
}

# Fonction : Reset complet
cmd_reset() {
    echo -e "${RED}  ATTENTION : Cette action va SUPPRIMER toutes les données !${NC}"
    echo ""
    read -p "Es-tu sûr de vouloir continuer ? (oui/non) : " confirm
    
    if [ "$confirm" = "oui" ]; then
        echo ""
        echo -e "${YELLOW}  Suppression des conteneurs et volumes...${NC}"
        $COMPOSE_CMD -f $COMPOSE_FILE down -v
        echo ""
        echo -e "${GREEN} Recréation de l'infrastructure...${NC}"
        $COMPOSE_CMD -f $COMPOSE_FILE up -d
        echo ""
        echo -e "${GREEN} Infrastructure recréée avec des bases vierges !${NC}"
        echo ""
        # Attendre que les conteneurs soient prêts
        echo "Attente de l'initialisation des bases de données..."
        sleep 10
        cmd_status
    else
        echo -e "${YELLOW}Opération annulée.${NC}"
    fi
}

# Fonction : Console PostgreSQL
cmd_psql() {
    echo -e "${BLUE} Connexion à PostgreSQL...${NC}"
    echo "   Bases disponibles : auth_db, users_db"
    echo "   Commandes utiles :"
    echo "     \\l          - Lister les bases"
    echo "     \\c auth_db  - Se connecter à auth_db"
    echo "     \\dt         - Lister les tables"
    echo "     \\q          - Quitter"
    echo ""
    docker exec -it codistrib-postgres psql -U ${POSTGRES_USER} -d postgres
}

# Fonction : Console MongoDB
cmd_mongo() {
    echo -e "${BLUE} Connexion à MongoDB...${NC}"
    echo "   Bases disponibles : messaging_db, location_db, alerts_db"
    echo "   Commandes utiles :"
    echo "     show dbs                    - Lister les bases"
    echo "     use messaging_db            - Changer de base"
    echo "     show collections            - Lister les collections"
    echo "     db.messages.find().limit(5) - Voir 5 messages"
    echo "     exit                        - Quitter"
    echo ""
    docker exec -it codistrib-mongo mongosh -u ${MONGO_USER} -p ${MONGO_PASSWORD} --authenticationDatabase admin
}

# Fonction : Console Redis
cmd_redis() {
    echo -e "${BLUE} Connexion à Redis...${NC}"
    echo "   Commandes utiles :"
    echo "     KEYS *      - Lister toutes les clés"
    echo "     GET key     - Obtenir une valeur"
    echo "     INFO        - Informations serveur"
    echo "     QUIT        - Quitter"
    echo ""
    docker exec -it codistrib-redis redis-cli -a ${REDIS_PASSWORD}
}

# Fonction : Afficher l'aide
cmd_help() {
    echo -e "${BLUE} Commandes disponibles :${NC}"
    echo ""
    echo "  ./dev.sh start     Démarre l'infrastructure"
    echo "  ./dev.sh stop      Arrête l'infrastructure"
    echo "  ./dev.sh restart   Redémarre l'infrastructure"
    echo "  ./dev.sh status    Affiche l'état des conteneurs"
    echo "  ./dev.sh logs      Affiche les logs (Ctrl+C pour quitter)"
    echo "  ./dev.sh reset     Supprime tout et recrée (perte de données)"
    echo "  ./dev.sh psql      Ouvre une console PostgreSQL"
    echo "  ./dev.sh mongo     Ouvre une console MongoDB"
    echo "  ./dev.sh redis     Ouvre une console Redis"
    echo "  ./dev.sh help      Affiche cette aide"
    echo ""
}

detect_compose_command

show_logo

check_docker

check_env

source .env 2>/dev/null; set +a

# Exécuter la commande demandée
case "${1:-help}" in
    start)
        cmd_start
        ;;
    stop)
        cmd_stop
        ;;
    restart)
        cmd_restart
        ;;
    status)
        cmd_status
        ;;
    logs)
        cmd_logs
        ;;
    reset)
        cmd_reset
        ;;
    psql)
        cmd_psql
        ;;
    mongo)
        cmd_mongo
        ;;
    redis)
        cmd_redis
        ;;
    help|*)
        cmd_help
        ;;
esac