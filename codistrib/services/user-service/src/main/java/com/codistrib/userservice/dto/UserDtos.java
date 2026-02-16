package com.codistrib.userservice.dto;

import com.codistrib.userservice.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDtos {

    /**
     * DTO pour créer un User
     * Utilisé par Auth Service lors de l'inscription
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private UUID authId;           // UUID depuis Auth Service
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePictureUrl;
        private UUID createdBy;        // Helper ou Admin qui crée
    }

    /**
     * DTO pour mettre à jour un User
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private UUID userId;
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePictureUrl;
        private UserStatus status;
    }

    /**
     * DTO de réponse pour un User
     * Envoyé via gRPC aux autres services
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private UUID userId;
        private UUID authId;
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePictureUrl;
        private UserStatus status;
        private UUID createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * DTO simplifié pour les snapshots dans Messaging Service
     * Contient juste les infos essentielles pour affichage
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSnapshot {
        private UUID userId;
        private String name;
        private String profilePictureUrl;
        private String role;  // PERSONNE_DI, HELPER, ADMIN
    }

    /**
     * DTO pour vérifier l'existence d'un user
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserExistsResponse {
        private UUID userId;
        private boolean exists;
        private UserStatus status;
    }

    /**
     * DTO pour la validation d'un user
     * Utilisé par Auth Service et Messaging Service
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateUserRequest {
        private UUID userId;
    }

    /**
     * DTO de réponse pour la validation
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateUserResponse {
        private boolean valid;
        private UUID userId;
        private String name;
        private UserStatus status;
        private String message;
    }
}