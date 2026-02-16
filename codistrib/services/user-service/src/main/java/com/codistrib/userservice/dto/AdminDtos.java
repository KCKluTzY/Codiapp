package com.codistrib.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AdminDtos {

    /**
     * DTO pour créer un Admin
     * Utilisé par un SUPER_ADMIN pour créer d'autres admins
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateAdminRequest {
        // Infos User
        @NotBlank(message = "Le nom est obligatoire")
        private String name;
        
        @NotBlank(message = "Le téléphone est obligatoire")
        private String phoneNumber;
        
        @NotNull(message = "La date de naissance est obligatoire")
        private LocalDate birthDate;
        
        // Infos Admin
        private UUID organizationId;  // null si SUPER_ADMIN
        
        @NotNull(message = "Les permissions sont obligatoires")
        private List<String> permissions;
        
        @NotBlank(message = "Le niveau de rôle est obligatoire")
        private String roleLevel;  // SUPER_ADMIN, ORG_ADMIN
        
        // Auth info (injecté depuis Auth Service)
        private UUID authId;
        
        // Créé par (SUPER_ADMIN)
        private UUID createdBy;
    }

    /**
     * DTO pour mettre à jour un Admin
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAdminRequest {
        private UUID userId;
        private String name;
        private String phoneNumber;
        private UUID organizationId;
        private List<String> permissions;
        private String roleLevel;
    }

    /**
     * DTO de réponse pour un Admin
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminResponse {
        private UUID userId;
        private UUID authId;
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePictureUrl;
        
        // Infos Admin
        private UUID organizationId;
        private String organizationName;  // Si applicable
        private List<String> permissions;
        private String roleLevel;
        
        // Métadonnées
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * DTO liste simplifiée d'Admins
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminSummary {
        private UUID userId;
        private String name;
        private String roleLevel;
        private UUID organizationId;
        private List<String> permissions;
    }

    /**
     * DTO pour vérifier les permissions d'un Admin
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPermissionRequest {
        @NotNull(message = "L'ID de l'admin est obligatoire")
        private UUID adminId;
        
        @NotBlank(message = "La permission à vérifier est obligatoire")
        private String permission;
    }

    /**
     * DTO de réponse pour la vérification de permission
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckPermissionResponse {
        private UUID adminId;
        private String permission;
        private boolean hasPermission;
        private String roleLevel;
        private String message;
    }

    /**
     * DTO pour les statistiques d'un Admin
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminStats {
        private UUID adminId;
        private String adminName;
        private String roleLevel;
        private Integer helpersValidated;
        private Integer helpersRejected;
        private Integer credentialsApproved;
        private Integer credentialsRejected;
        private LocalDateTime lastActionAt;
    }

    /**
     * DTO pour le dashboard admin
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminDashboard {
        // Statistiques globales
        private Integer totalUsers;
        private Integer totalPersonsDI;
        private Integer totalHelpers;
        private Integer totalAdmins;
        
        // En attente de validation
        private Integer helpersPending;
        private Integer credentialRequestsPending;
        
        // Récents
        private Integer newUsersLast7Days;
        private Integer newHelpersLast7Days;
        
        // Listes
        private List<HelperDtos.HelperSummary> recentHelpersPending;
        private List<CredentialRequestDtos.CredentialRequestSummary> recentCredentialRequestsPending;
    }

    /**
     * DTO pour les permissions disponibles dans le système
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailablePermissions {
        private List<PermissionInfo> permissions;
    }

    /**
     * Info sur une permission
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionInfo {
        private String code;              // Ex: "VALIDATE_HELPERS"
        private String displayName;       // Ex: "Valider les aidants"
        private String description;       // Description détaillée
        private String category;          // Ex: "HELPER_MANAGEMENT", "USER_MANAGEMENT"
    }
}