package com.codistrib.userservice.dto;

import com.codistrib.userservice.domain.enums.AvailabilityStatus;
import com.codistrib.userservice.domain.enums.ValidationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HelperDtos {

    /**
     * DTO pour l'auto-inscription d'un Helper
     * Utilisé lors du register via Auth Service
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateHelperRequest {
        // Infos User
        @NotBlank(message = "Le nom est obligatoire")
        private String name;
        
        @NotBlank(message = "Le téléphone est obligatoire")
        private String phoneNumber;
        
        @NotNull(message = "La date de naissance est obligatoire")
        private LocalDate birthDate;
        
        // Infos Helper spécifiques
        @NotNull(message = "Les spécialisations sont obligatoires")
        private List<String> specializations;
        
        private Map<String, Object> certifications;
        
        @NotNull(message = "Les langues sont obligatoires")
        private List<String> languages;
        
        private Integer maxConcurrentAssists;
        
        // Auth info (injecté depuis Auth Service)
        private UUID authId;
    }

    /**
     * DTO pour mettre à jour le profil d'un Helper
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateHelperRequest {
        private UUID userId;
        private String name;
        private String phoneNumber;
        private List<String> specializations;
        private Map<String, Object> certifications;
        private List<String> languages;
        private Integer maxConcurrentAssists;
        private String profilePictureUrl;
    }

    /**
     * DTO pour mettre à jour la disponibilité
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateAvailabilityRequest {
        private UUID userId;
        
        @NotNull(message = "Le statut de disponibilité est obligatoire")
        private AvailabilityStatus availabilityStatus;
    }

    /**
     * DTO de réponse pour un Helper
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelperResponse {
        private UUID userId;
        private UUID authId;
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePictureUrl;
        
        // Infos Helper
        private List<String> specializations;
        private AvailabilityStatus availabilityStatus;
        private BigDecimal rating;
        private Integer totalAssists;
        private ValidationStatus validationStatus;
        private UUID validatedBy;
        private String validatedByName;  // Nom de l'admin validateur
        private LocalDateTime validatedAt;
        private Map<String, Object> certifications;
        private List<String> languages;
        private Integer maxConcurrentAssists;
        private Integer currentAssignedPersons;  // Nombre de PersonDI assignées
        
        // Métadonnées
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * DTO liste simplifiée de Helpers (pour affichage liste)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelperSummary {
        private UUID userId;
        private String name;
        private String profilePictureUrl;
        private List<String> specializations;
        private AvailabilityStatus availabilityStatus;
        private ValidationStatus validationStatus;
        private BigDecimal rating;
        private Integer totalAssists;
        private Integer currentAssignedPersons;
        private boolean canTakeMore;  // capacity disponible
    }

    /**
     * DTO pour la validation d'un Helper par un Admin
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateHelperRequest {
        @NotNull(message = "L'ID du Helper est obligatoire")
        private UUID helperId;
        
        @NotNull(message = "La décision (approved/rejected) est obligatoire")
        private boolean approved;
        
        private String notes;  // Notes de l'admin
        
        // Admin qui valide (injecté depuis JWT)
        private UUID validatedBy;
    }

    /**
     * DTO de réponse pour la validation
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateHelperResponse {
        private UUID helperId;
        private String helperName;
        private ValidationStatus validationStatus;
        private String message;
        private LocalDateTime validatedAt;
    }

    /**
     * DTO pour obtenir les Helpers disponibles (matching)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableHelpersRequest {
        private String specialization;  // Filtre optionnel
        private String language;        // Filtre optionnel
        private BigDecimal minRating;   // Filtre optionnel
    }

    /**
     * DTO de réponse pour les Helpers disponibles
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailableHelpersResponse {
        private int totalAvailable;
        private List<HelperSummary> helpers;
    }

    /**
     * DTO pour les statistiques d'un Helper
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelperStats {
        private UUID helperId;
        private String helperName;
        private Integer totalAssists;
        private BigDecimal rating;
        private Integer currentAssignedPersons;
        private Integer personsWithCredentials;
        private Integer personsPendingCredentials;
        private LocalDateTime lastActiveAt;
    }
}