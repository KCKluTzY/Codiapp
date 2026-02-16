package com.codistrib.userservice.service;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.model.CredentialRequest;
import com.codistrib.userservice.dto.CredentialRequestDtos;

import java.util.List;
import java.util.UUID;

public interface CredentialRequestService {

    /**
     * Créer une nouvelle demande de credentials
     * Appelée automatiquement lors de la création d'une PersonDI
     */
    CredentialRequest createCredentialRequest(UUID userId, UUID requestedBy, String requestedEmail);

    /**
     * Récupérer une demande par son ID
     */
    CredentialRequest getCredentialRequestById(UUID requestId);

    /**
     * Traiter une demande (approuver ou rejeter)
     */
    CredentialRequestDtos.ReviewCredentialRequestResponse reviewCredentialRequest(
            CredentialRequestDtos.ReviewCredentialRequestRequest request);

    /**
     * Récupérer toutes les demandes par statut
     */
    List<CredentialRequest> getCredentialRequestsByStatus(CredentialRequestStatus status);

    /**
     * Récupérer toutes les demandes en attente
     * Triées par date (plus anciennes en premier)
     */
    CredentialRequestDtos.PendingCredentialRequestsResponse getPendingRequests();

    /**
     * Récupérer les demandes d'un Helper spécifique
     */
    List<CredentialRequest> getCredentialRequestsByHelper(UUID requestedBy);

    /**
     * Récupérer la demande active (PENDING) pour une PersonDI
     */
    CredentialRequest getPendingRequestByUserId(UUID userId);

    /**
     * Vérifier si une demande PENDING existe pour une PersonDI
     */
    boolean hasPendingRequest(UUID userId);

    /**
     * Vérifier si un email est déjà demandé
     */
    boolean isEmailAlreadyRequested(String email);

    /**
     * Récupérer les statistiques des demandes
     */
    CredentialRequestDtos.CredentialRequestStats getStats();
}