package com.codistrib.userservice.service;

import com.codistrib.userservice.domain.enums.UserStatus;
import com.codistrib.userservice.domain.model.User;
import com.codistrib.userservice.dto.UserDtos;

import java.util.List;
import java.util.UUID;

public interface UserService {

    /**
     * Créer un nouvel utilisateur
     * Appelé par Auth Service lors de l'inscription
     */
    User createUser(UserDtos.CreateUserRequest request);

    /**
     * Récupérer un utilisateur par son ID
     */
    User getUserById(UUID userId);

    /**
     * Récupérer un utilisateur par son auth_id
     */
    User getUserByAuthId(UUID authId);

    /**
     * Mettre à jour un utilisateur
     */
    User updateUser(UserDtos.UpdateUserRequest request);

    /**
     * Mettre à jour le statut d'un utilisateur
     */
    User updateUserStatus(UUID userId, UserStatus status);

    /**
     * Mettre à jour l'auth_id d'un utilisateur
     */
    User updateAuthId(UUID userId, UUID authId);

    /**
     * Récupérer tous les utilisateurs par statut
     */
    List<User> getUsersByStatus(UserStatus status);

    /**
     * Récupérer les utilisateurs créés par un utilisateur spécifique
     */
    List<User> getUsersCreatedBy(UUID createdBy);

    /**
     * Vérifier si un utilisateur existe
     */
    boolean userExists(UUID userId);

    /**
     * Vérifier si un auth_id existe
     */
    boolean authIdExists(UUID authId);

    /**
     * Valider un utilisateur (pour appels gRPC)
     */
    UserDtos.ValidateUserResponse validateUser(UUID userId);

    /**
     * Récupérer un snapshot utilisateur (pour Messaging Service)
     */
    UserDtos.UserSnapshot getUserSnapshot(UUID userId);

    /**
     * Supprimer un utilisateur (soft delete)
     */
    void deleteUser(UUID userId);

    /**
     * Compter les utilisateurs par statut
     */
    long countByStatus(UserStatus status);
}