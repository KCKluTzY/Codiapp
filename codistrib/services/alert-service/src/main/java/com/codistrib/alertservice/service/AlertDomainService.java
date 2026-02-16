package com.codistrib.alertservice.service;

import com.codistrib.alertservice.domain.enums.AlertStatus;
import com.codistrib.alertservice.domain.model.Alert;

import java.util.List;

public interface AlertDomainService {
    /**
     * Crée une alerte OPEN pour une personne DI.
     * @param personId id de la personne (obligatoire)
     * @param type     type d’alerte (ex: SOS) (obligatoire)
     * @param message  description courte (optionnelle)
     * @param lat      latitude (optionnelle)
     * @param lon      longitude (optionnelle)
     */
    Alert create(String personId, String type, String message, Double lat, Double lon);

    /**
     * Assigne un aidant sur une alerte à l’état OPEN → ASSIGNED.
     * @param alertId id de l’alerte (obligatoire)
     * @param helperId id de l’aidant (obligatoire)
     */
    Alert assign(String alertId, String helperId);

    /**
     * Résout une alerte ASSIGNED → RESOLVED. Seul l’aidant assigné peut la résoudre.
     */
    Alert resolve(String alertId, String helperId);

    // Lecture
    Alert getById(String alertId);
    List<Alert> listByPerson(String personId);
    List<Alert> listByHelper(String helperId);
    List<Alert> listByStatus(AlertStatus status);
}