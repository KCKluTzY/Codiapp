package com.codistrib.userservice.service;

import com.codistrib.userservice.domain.enums.AvailabilityStatus;
import com.codistrib.userservice.domain.enums.ValidationStatus;
import com.codistrib.userservice.domain.model.Helper;
import com.codistrib.userservice.dto.HelperDtos;

import java.util.List;
import java.util.UUID;

public interface HelperService {

    /**
     * Créer un nouveau Helper
     * Appelé lors de l'auto-inscription via Auth Service
     */
    Helper createHelper(HelperDtos.CreateHelperRequest request);

    /**
     * Récupérer un Helper par son user_id
     */
    Helper getHelperById(UUID userId);

    /**
     * Mettre à jour le profil d'un Helper
     */
    Helper updateHelper(HelperDtos.UpdateHelperRequest request);

    /**
     * Mettre à jour la disponibilité d'un Helper
     */
    Helper updateAvailability(HelperDtos.UpdateAvailabilityRequest request);

    /**
     * Valider un Helper (par un Admin)
     * Change le statut de PENDING à APPROVED ou REJECTED
     */
    HelperDtos.ValidateHelperResponse validateHelper(HelperDtos.ValidateHelperRequest request);

    /**
     * Vérifier si un Helper est validé
     */
    boolean isHelperValidated(UUID userId);

    /**
     * Récupérer tous les Helpers par statut de validation
     */
    List<Helper> getHelpersByValidationStatus(ValidationStatus validationStatus);

    /**
     * Récupérer tous les Helpers par statut de disponibilité
     */
    List<Helper> getHelpersByAvailabilityStatus(AvailabilityStatus availabilityStatus);

    /**
     * Récupérer les Helpers disponibles et validés
     * Pour le matching avec PersonDI
     */
    HelperDtos.AvailableHelpersResponse getAvailableHelpers(HelperDtos.AvailableHelpersRequest request);

    /**
     * Récupérer les Helpers avec capacité disponible
     */
    List<Helper> getHelpersWithCapacity();

    /**
     * Récupérer les Helpers par spécialisation
     */
    List<Helper> getHelpersBySpecialization(String specialization);

    /**
     * Récupérer les Helpers par langue
     */
    List<Helper> getHelpersByLanguage(String language);

    /**
     * Récupérer les statistiques d'un Helper
     */
    HelperDtos.HelperStats getHelperStats(UUID userId);

    /**
     * Incrémenter le compteur d'assistances d'un Helper
     */
    void incrementTotalAssists(UUID userId);

    /**
     * Mettre à jour la note d'un Helper
     */
    void updateRating(UUID userId, Double newRating);

    /**
     * Compter le nombre de Helpers par statut de validation
     */
    long countByValidationStatus(ValidationStatus validationStatus);
}