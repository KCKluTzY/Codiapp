package com.codistrib.userservice.dto;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class CredentialRequestDtos {

    /**
     * DTO pour créer une demande de credentials
     * Automatiquement créé lors de la création d'une PersonDI
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCredentialRequestRequest {
        @NotNull(message = "L'ID de la PersonDI est obligatoire")
        private UUID userId;  // PersonDI concernée
        
        @NotNull(message = "L'ID du demandeur est obligatoire")
        private UUID requestedBy;  // Helper qui demande
        
        @Email(message = "L'email doit être valide")
        @NotBlank(message = "L'email est obligatoire")
        private String requestedEmail;  // Email pour les credentials
    }

    /**
     * DTO pour approuver/rejeter une demande de credentials
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewCredentialRequestRequest {
        @NotNull(message = "L'ID de la demande est obligatoire")
        private UUID requestId;
        
        @NotNull(message = "La décision (approved/rejected) est obligatoire")
        private boolean approved;
        
        @Email(message = "L'email doit être valide")
        private String email;  // Admin peut modifier l'email proposé
        
        private String adminNotes;
        
        private boolean sendCredentialsByEmail;  // Envoyer email au tuteur
        
        // Admin qui traite (injecté depuis JWT)
        private UUID reviewedBy;
    }

    /**
     * DTO de réponse pour une demande de credentials
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CredentialRequestResponse {
        private UUID requestId;
        private UUID userId;  // PersonDI
        private String personName;
        private UUID requestedBy;  // Helper
        private String helperName;
        private String requestedEmail;
        private CredentialRequestStatus status;
        private String adminNotes;
        private UUID reviewedBy;  // Admin
        private String reviewedByName;
        private LocalDateTime reviewedAt;
        private LocalDateTime createdAt;
    }

    /**
     * DTO liste simplifiée de demandes (pour affichage liste)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CredentialRequestSummary {
        private UUID requestId;
        private UUID userId;
        private String personName;
        private String helperName;
        private String requestedEmail;
        private CredentialRequestStatus status;
        private LocalDateTime createdAt;
        private int daysSinceCreated;  // Nombre de jours depuis la demande
    }

    /**
     * DTO de réponse après traitement d'une demande
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewCredentialRequestResponse {
        private UUID requestId;
        private UUID userId;
        private String personName;
        private CredentialRequestStatus status;
        private UUID authId;  // Si approved, l'auth_id créé
        private String temporaryPassword;  // Si approved, le mot de passe temporaire
        private boolean credentialsSent;  // Si email envoyé
        private String message;
    }

    /**
     * DTO pour la liste des demandes en attente
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingCredentialRequestsResponse {
        private int totalPending;
        private int oldestDays;  // Demande la plus ancienne (en jours)
        private java.util.List<CredentialRequestSummary> requests;
    }

    /**
     * DTO pour les statistiques des demandes
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CredentialRequestStats {
        private Integer totalRequests;
        private Integer pendingRequests;
        private Integer approvedRequests;
        private Integer rejectedRequests;
        private Double approvalRate;  // Taux d'approbation (%)
        private Integer averageProcessingDays;  // Durée moyenne de traitement
    }

    /**
     * DTO pour filtrer les demandes
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CredentialRequestFilter {
        private CredentialRequestStatus status;
        private UUID requestedBy;  // Filtrer par Helper
        private UUID reviewedBy;   // Filtrer par Admin
        private LocalDateTime createdAfter;
        private LocalDateTime createdBefore;
        private Integer olderThanDays;  // Demandes plus vieilles que X jours
    }
}