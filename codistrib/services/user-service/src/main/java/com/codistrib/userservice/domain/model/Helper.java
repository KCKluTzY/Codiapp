package com.codistrib.userservice.domain.model;

import com.codistrib.userservice.domain.enums.AvailabilityStatus;
import com.codistrib.userservice.domain.enums.ValidationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Entité Helper - Aidant
 * 
 * Table spécifique aux utilisateurs Helper (aidants)
 * Hérite du User via une relation One-to-One
 */
@Entity
@Table(name = "helpers", indexes = {
    @Index(name = "idx_helpers_validation_status", columnList = "validation_status"),
    @Index(name = "idx_helpers_availability", columnList = "availability_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Helper {

    /**
     * Clé primaire = user_id (même que dans User)
     */
    @Id
    @Column(name = "user_id")
    private UUID userId;

    /**
     * Relation One-to-One avec User
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Spécialisations du Helper
     * Exemple: ['TRANSPORT', 'DAILY_TASKS', 'MEDICAL']
     * 
     * PostgreSQL array (TEXT[])
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "specializations", columnDefinition = "text[]")
    private List<String> specializations;

    /**
     * Statut de disponibilité
     * AVAILABLE, BUSY, OFFLINE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.OFFLINE;

    /**
     * Note moyenne du Helper (0.0 à 5.0)
     */
    @Column(name = "rating", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    /**
     * Nombre total d'assistances effectuées
     */
    @Column(name = "total_assists")
    @Builder.Default
    private Integer totalAssists = 0;

    /**
     * Statut de validation par l'admin
     * PENDING, APPROVED, REJECTED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false, length = 50)
    @Builder.Default
    private ValidationStatus validationStatus = ValidationStatus.PENDING;

    /**
     * ID de l'admin qui a validé ce Helper
     */
    @Column(name = "validated_by")
    private UUID validatedBy;

    /**
     * Date de validation
     */
    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    /**
     * Certifications du Helper (JSON)
     * Exemple: { "firstAid": true, "disability": true, "transport": false }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "certifications", columnDefinition = "jsonb")
    private Map<String, Object> certifications;

    /**
     * Langues parlées
     * Exemple: ['fr', 'en', 'es']
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "languages", columnDefinition = "text[]")
    @Builder.Default
    private List<String> languages = List.of("fr");

    /**
     * Nombre maximum d'assistances simultanées
     */
    @Column(name = "max_concurrent_assists")
    @Builder.Default
    private Integer maxConcurrentAssists = 3;

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

    /**
     * Relation One-to-Many avec PersonDI
     * Un Helper peut être assigné à plusieurs PersonDI
     */
    @OneToMany(mappedBy = "assignedHelper", fetch = FetchType.LAZY)
    private List<PersonDI> assignedPersons;
}