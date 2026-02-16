#!/bin/bash

set -e
set -u

create_database() {
    local database=$1
    echo ""
    echo "Création de la base de données : $database"
    echo "   ----------------------------------------"

    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -d postgres <<-EOSQL
        -- Vérifie si la base existe, si non la crée
        SELECT 'CREATE DATABASE $database'
        WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$database')\gexec
        
        -- Donne tous les privilèges à l'utilisateur
        GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
    
    echo "   Base '$database' créée avec succès !"
}

echo ""
echo "CODI'strib - Initialisation PostgreSQL"
echo "   ----------------------------------------"

create_database "auth_db"

create_database "users_db"

echo ""
echo "CODI'strib - Initialisation terminée avec succès !"
echo "Bases créées :"
echo "    • auth_db    → pour auth-service"
echo "    • users_db   → pour user-service"
echo "   ----------------------------------------"