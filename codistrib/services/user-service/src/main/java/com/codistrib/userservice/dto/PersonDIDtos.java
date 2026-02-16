package com.codistrib.userservice.dto;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.enums.DisabilityLevel;
import com.codistrib.userservice.domain.enums.PreferredCommunication;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class PersonDIDtos {

    /**
     * DTO pour créer une PersonDI (par un Helper)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePersonDIRequest {
        // Infos User
        @NotBlank(message = "Le nom est obligatoire")
        private String name;
        
        private String phoneNumber;
        
        @NotNull(message = "La date de naissance est obligatoire")
        private LocalDate birthDate;
        
        // Infos PersonDI spécifiques
        @NotNull(message = "Le niveau de déficience est obligatoire")
        private DisabilityLevel disabilityLevel;
        
        @NotBlank(message = "Le nom du tuteur est obligatoire")
        private String guardianName;
        
        @NotBlank(message = "Le téléphone du tuteur est obligatoire")
        private String guardianPhone;
        
        @Email(message = "L'email du tuteur doit être valide")
        @NotBlank(message = "L'email du tuteur est obligatoire")
        private String guardianEmail;
        
        private PreferredCommunication preferredCommunication;
        private Map<String, Object> accessibilitySettings;
        
        // Email pour les credentials
        @Email(message = "L'email pour les credentials doit être valide")
        @NotBlank(message = "L'email pour les credentials est obligatoire")
        private String requestedEmail;
        
        // Helper qui crée (injecté depuis JWT)
        private UUID createdBy;
    }

    /**
     * DTO pour mettre à jour une PersonDI
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePersonDIRequest {
        private UUID userId;
        private String name;
        private String phoneNumber;
        private String guardianName;
        private String guardianPhone;
        private String guardianEmail;
        private PreferredCommunication preferredCommunication;
        private Map<String, Object> accessibilitySettings;
        private UUID assignedHelperId;
    }

    /**
     * DTO de réponse pour une PersonDI
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonDIResponse {
        private UUID userId;
        private UUID authId;
        private String name;
        private String phoneNumber;
        private LocalDate birthDate;
        private String profilePictureUrl;
        
        // Infos PersonDI
        private DisabilityLevel disabilityLevel;
        private UUID emergencyContactId;
        private String guardianName;
        private String guardianPhone;
        private String guardianEmail;
        private PreferredCommunication preferredCommunication;
        private Map<String, Object> accessibilitySettings;
        private UUID assignedHelperId;
        private String assignedHelperName;  // Nom du Helper assigné
        private CredentialRequestStatus credentialRequestStatus;
        
        // Métadonnées
        private UUID createdBy;
        private String createdByName;  // Nom du créateur
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * DTO liste simplifiée de PersonDI (pour affichage liste)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonDISummary {
        private UUID userId;
        private String name;
        private String profilePictureUrl;
        private DisabilityLevel disabilityLevel;
        private UUID assignedHelperId;
        private String assignedHelperName;
        private CredentialRequestStatus credentialRequestStatus;
        private boolean hasCredentials;  // authId != null
    }

    /**
     * DTO pour la liste des PersonDI d'un Helper
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HelperPersonDIListResponse {
        private UUID helperId;
        private String helperName;
        private int totalPersons;
        private int personsWithCredentials;
        private int personsPendingCredentials;
        private java.util.List<PersonDISummary> persons;
    }
}