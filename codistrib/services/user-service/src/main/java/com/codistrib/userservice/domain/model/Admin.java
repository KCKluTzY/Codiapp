package com.codistrib.userservice.domain.model;

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
import java.util.List;
import java.util.UUID;

/**
 * Entité Admin - Administrateur
 * 
 * Table spécifique aux utilisateurs Admin
 * Hérite du User via une relation One-to-One
 */
@Entity
@Table(name = "admins", indexes = {
    @Index(name = "idx_admins_organization", columnList = "organization_id"),
    @Index(name = "idx_admins_role_level", columnList = "role_level")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

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
     * ID de l'organisation (si plusieurs organisations utilisent le système)
     * Peut être null pour SUPER_ADMIN
     */
    @Column(name = "organization_id")
    private UUID organizationId;

    /**
     * Permissions spécifiques de l'admin
     * Exemple: ['VALIDATE_HELPERS', 'VALIDATE_CREDENTIALS', 'MANAGE_USERS', 'VIEW_ANALYTICS']
     * 
     * PostgreSQL array (TEXT[])
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "permissions", columnDefinition = "text[]")
    private List<String> permissions;

    /**
     * Niveau d'admin
     * SUPER_ADMIN: toutes permissions sur tout le système
     * ORG_ADMIN: permissions sur son organisation uniquement
     */
    @Column(name = "role_level", nullable = false, length = 20)
    @Builder.Default
    private String roleLevel = "ORG_ADMIN";

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