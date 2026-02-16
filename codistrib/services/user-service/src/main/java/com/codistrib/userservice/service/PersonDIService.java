package com.codistrib.userservice.service;

import com.codistrib.userservice.domain.enums.CredentialRequestStatus;
import com.codistrib.userservice.domain.model.PersonDI;
import com.codistrib.userservice.dto.PersonDIDtos;

import java.util.List;
import java.util.UUID;

public interface PersonDIService {

    /**
     * Créer une nouvelle PersonDI
     * Appelé par un Helper validé
     * Crée automatiquement une CredentialRequest
     */
    PersonDI createPersonDI(PersonDIDtos.CreatePersonDIRequest request);

    /**
     * Récupérer une PersonDI par son user_id
     */
    PersonDI getPersonDIById(UUID userId);

    /**
     * Mettre à jour une PersonDI
     */
    PersonDI updatePersonDI(PersonDIDtos.UpdatePersonDIRequest request);

    /**
     * Récupérer toutes les PersonDI assignées à un Helper
     */
    PersonDIDtos.HelperPersonDIListResponse getPersonsDIByHelper(UUID helperId);

    /**
     * Récupérer les PersonDI par statut de demande de credentials
     */
    List<PersonDI> getPersonsDIByCredentialRequestStatus(CredentialRequestStatus status);

    /**
     * Récupérer les PersonDI sans credentials
     */
    List<PersonDI> getPersonsDIWithoutCredentials();

    /**
     * Récupérer les PersonDI avec credentials actifs
     */
    List<PersonDI> getActivePersonsDIWithCredentials();

    /**
     * Récupérer les PersonDI créées par un Helper
     */
    List<PersonDI> getPersonsDICreatedBy(UUID createdBy);

    /**
     * Mettre à jour le statut de demande de credentials
     */
    PersonDI updateCredentialRequestStatus(UUID userId, CredentialRequestStatus status);

    /**
     * Assigner un Helper à une PersonDI
     */
    PersonDI assignHelper(UUID personDIId, UUID helperId);

    /**
     * Vérifier si un email tuteur existe déjà
     */
    boolean guardianEmailExists(String guardianEmail);

    /**
     * Compter le nombre de PersonDI assignées à un Helper
     */
    long countByAssignedHelper(UUID helperId);

    /**
     * Récupérer un résumé d'une PersonDI
     */
    PersonDIDtos.PersonDISummary getPersonDISummary(UUID userId);
}