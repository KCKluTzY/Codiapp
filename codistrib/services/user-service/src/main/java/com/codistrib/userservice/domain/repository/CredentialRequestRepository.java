package com.codistrib.userservice.domain.repository;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.model.CredentialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialRequestRepository extends JpaRepository<CredentialRequest, UUID> {

    /**
     * Rechercher une demande par son ID
     */
    Optional<CredentialRequest> findByRequestId(UUID requestId);

    /**
     * Rechercher les demandes par statut
     */
    List<CredentialRequest> findByStatus(CredentialRequestStatus status);

    /**
     * Rechercher les demandes pour un utilisateur (PersonDI) spécifique
     * Une PersonDI peut avoir plusieurs demandes (rejetées puis re-demandées)
     */
    List<CredentialRequest> findByUserId(UUID userId);

    /**
     * Rechercher la demande active (PENDING) pour une PersonDI
     */
    @Query("SELECT cr FROM CredentialRequest cr WHERE cr.userId = :userId " +
           "AND cr.status = 'PENDING'")
    Optional<CredentialRequest> findPendingRequestByUserId(@Param("userId") UUID userId);

    /**
     * Rechercher les demandes créées par un Helper spécifique
     */
    List<CredentialRequest> findByRequestedBy(UUID requestedBy);

    /**
     * Rechercher les demandes traitées par un Admin spécifique
     */
    List<CredentialRequest> findByReviewedBy(UUID reviewedBy);

    /**
     * Rechercher les demandes en attente (PENDING)
     */
    @Query("SELECT cr FROM CredentialRequest cr WHERE cr.status = 'PENDING' " +
           "ORDER BY cr.createdAt ASC")
    List<CredentialRequest> findPendingRequestsOrderedByDate();

    /**
     * Rechercher les demandes récentes (créées après une certaine date)
     */
    List<CredentialRequest> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Compter le nombre de demandes par statut
     */
    long countByStatus(CredentialRequestStatus status);

    /**
     * Vérifier si une demande PENDING existe déjà pour une PersonDI
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END " +
           "FROM CredentialRequest cr WHERE cr.userId = :userId " +
           "AND cr.status = 'PENDING'")
    boolean hasPendingRequest(@Param("userId") UUID userId);

    /**
     * Vérifier si un email est déjà demandé dans une requête PENDING
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END " +
           "FROM CredentialRequest cr WHERE cr.requestedEmail = :email " +
           "AND cr.status = 'PENDING'")
    boolean isEmailAlreadyRequested(@Param("email") String email);

    /**
     * Rechercher les demandes en attente depuis plus de X jours
     */
    @Query("SELECT cr FROM CredentialRequest cr WHERE cr.status = 'PENDING' " +
           "AND cr.createdAt < :threshold " +
           "ORDER BY cr.createdAt ASC")
    List<CredentialRequest> findOldPendingRequests(@Param("threshold") LocalDateTime threshold);

    /**
     * Statistiques : Compter les demandes approuvées/rejetées par admin
     */
    long countByReviewedByAndStatus(UUID adminId, CredentialRequestStatus status);
}