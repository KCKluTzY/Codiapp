package com.codistrib.userservice.domain.model;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.enums.DisabilityLevel;
import com.codistrib.userservice.domain.enums.PreferredCommunication;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entité PersonDI - Personne avec Déficience Intellectuelle
 * 
 * Table spécifique aux utilisateurs PersonDI
 * Hérite du User via une relation One-to-One
 */
@Entity
@Table(name = "persons_di", indexes = {
    @Index(name = "idx_persons_di_assigned_helper", columnList = "assigned_helper_id"),
    @Index(name = "idx_persons_di_credential_status", columnList = "credential_request_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDI {

    /**
     * Clé primaire = user_id (même que dans User)
     * Relation One-to-One avec User
     */
    @Id
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Relation One-to-One avec User
     * CascadeType.ALL : si on supprime PersonDI, on supprime aussi User
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId  // Utilise userId comme FK et PK
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Niveau de déficience intellectuelle
     * MILD, MODERATE, SEVERE, PROFOUND
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "disability_level", nullable = false, length = 50)
    private DisabilityLevel disabilityLevel;

    /**
     * Contact d'urgence (référence vers un autre User)
     * Peut être null au départ
     */
    @Column(name = "emergency_contact_id")
    private UUID emergencyContactId;

    /**
     * Nom du tuteur/famille
     */
    @Column(name = "guardian_name", nullable = false, length = 255)
    private String guardianName;

    /**
     * Téléphone du tuteur
     */
    @Column(name = "guardian_phone", nullable = false, length = 20)
    private String guardianPhone;

    /**
     * Email du tuteur (pour recevoir les credentials)
     */
    @Column(name = "guardian_email", length = 255)
    private String guardianEmail;

    /**
     * Mode de communication préféré
     * TEXT, PICTOGRAM, VOICE, VIDEO
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_communication", length = 50)
    @Builder.Default
    private PreferredCommunication preferredCommunication = PreferredCommunication.TEXT;

    /**
     * Paramètres d'accessibilité (JSON)
     * Exemple: { "fontSize": "LARGE", "highContrast": true, "voiceGuidance": true }
     * 
     * Hibernate stocke automatiquement ce Map en JSON PostgreSQL (JSONB)
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "accessibility_settings", columnDefinition = "jsonb")
    private Map<String, Object> accessibilitySettings;

    /**
     * Helper principal assigné à cette PersonDI
     * Le Helper qui a créé le profil devient le Helper principal
     */
    @Column(name = "assigned_helper_id")
    private UUID assignedHelperId;

    /**
     * Relation ManyToOne avec Helper (optionnelle pour JPA, on utilise juste l'ID)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_helper_id", insertable = false, updatable = false)
    private Helper assignedHelper;

    /**
     * Statut de la demande de credentials
     * PENDING, APPROVED, REJECTED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "credential_request_status", nullable = false, length = 50)
    @Builder.Default
    private CredentialRequestStatus credentialRequestStatus = CredentialRequestStatus.PENDING;

    /**
     * Date de création
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de mise à jour
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}