package com.codistrib.userservice.domain.model;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité CredentialRequest - Demande de création de credentials
 * 
 * Trace les demandes de credentials pour les PersonDI
 * Un Helper crée un profil PersonDI → génère une CredentialRequest
 * Un Admin valide → credentials créés dans Auth Service
 */
@Entity
@Table(name = "credential_requests", indexes = {
    @Index(name = "idx_credential_requests_status", columnList = "status"),
    @Index(name = "idx_credential_requests_user", columnList = "user_id"),
    @Index(name = "idx_credential_requests_requester", columnList = "requested_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CredentialRequest {

    /**
     * Identifiant unique de la demande
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id", updatable = false, nullable = false)
    private UUID requestId;

    /**
     * ID de la PersonDI concernée
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Relation ManyToOne avec User (la PersonDI)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * ID du Helper qui a fait la demande
     */
    @Column(name = "requested_by", nullable = false)
    private UUID requestedBy;

    /**
     * Relation ManyToOne avec User (le Helper)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", insertable = false, updatable = false)
    private User requester;

    /**
     * Email proposé pour les credentials
     * (généralement l'email du tuteur)
     */
    @Column(name = "requested_email", nullable = false, length = 255)
    private String requestedEmail;

    /**
     * Statut de la demande
     * PENDING, APPROVED, REJECTED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private CredentialRequestStatus status = CredentialRequestStatus.PENDING;

    /**
     * Notes de l'admin (raison d'approbation/rejet)
     */
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    /**
     * ID de l'admin qui a traité la demande
     */
    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    /**
     * Relation ManyToOne avec Admin
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", insertable = false, updatable = false)
    private Admin reviewer;

    /**
     * Date de traitement de la demande
     */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * Date de création de la demande
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}