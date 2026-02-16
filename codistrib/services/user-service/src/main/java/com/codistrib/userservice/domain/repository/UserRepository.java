package com.codistrib.userservice.domain.repository;

import com.codistrib.userservice.domain.enums.UserStatus;
import com.codistrib.userservice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Rechercher un utilisateur par son auth_id
     */
    Optional<User> findByAuthId(UUID authId);

    /**
     * Rechercher un utilisateur par son nom
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Rechercher tous les utilisateurs ayant un certain statut
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Rechercher les utilisateurs créés par un utilisateur spécifique
     */
    List<User> findByCreatedBy(UUID createdBy);

    /**
     * Vérifier si un auth_id existe déjà
     */
    boolean existsByAuthId(UUID authId);

    /**
     * Requête personnalisée : Récupérer les utilisateurs actifs créés récemment
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' " +
           "AND u.createdAt >= CURRENT_TIMESTAMP - :daysAgo DAY " +
           "ORDER BY u.createdAt DESC")
    List<User> findRecentActiveUsers(@Param("daysAgo") int daysAgo);

    /**
     * Compter le nombre d'utilisateurs par statut
     */
    long countByStatus(UserStatus status);
}