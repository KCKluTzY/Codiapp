package com.codistrib.userservice.domain.model;

import com.codistrib.userservice.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_auth_id", columnList = "auth_id"),
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_created_by", columnList = "created_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Identifiant unique de l'utilisateur
     * Généré automatiquement par PostgreSQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    /**
     * Référence vers l'Auth Service (table auths)
     * NULL si les credentials n'ont pas encore été créés (cas PersonDI)
     */
    @Column(name = "auth_id", unique = true)
    private UUID authId;

    /**
     * Nom complet de l'utilisateur
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Numéro de téléphone
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Date de naissance
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * URL de la photo de profil (stockée dans /mnt/storage/profiles/)
     */
    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    /**
     * Statut de l'utilisateur
     * PENDING_CREDENTIALS, ACTIVE, SUSPENDED, INACTIVE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_CREDENTIALS;

    /**
     * ID de l'utilisateur qui a créé ce profil
     * Peut être un Helper (pour PersonDI) ou un Admin
     */
    @Column(name = "created_by")
    private UUID createdBy;

    /**
     * Date de création (automatique)
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière mise à jour (automatique)
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}