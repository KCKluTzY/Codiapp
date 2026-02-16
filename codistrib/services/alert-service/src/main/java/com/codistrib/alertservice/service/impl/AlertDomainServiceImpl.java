package com.codistrib.alertservice.service.impl;

import com.codistrib.alertservice.domain.enums.AlertStatus;
import com.codistrib.alertservice.domain.model.Alert;
import com.codistrib.alertservice.domain.repository.AlertRepository;
import com.codistrib.alertservice.service.AlertDomainService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AlertDomainServiceImpl implements AlertDomainService {
    private final AlertRepository repo;

    public AlertDomainServiceImpl(AlertRepository repo) {
        this.repo = repo;
    }

    @Override
    public Alert create(String personId, String type, String message, Double lat, Double lon) {
        // Validations d’entrée
        if (personId == null || personId.isBlank()) {
            throw new IllegalArgumentException("personId is required");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type is required");
        }

        Instant now = Instant.now();
        Alert a = new Alert();
        a.setPersonId(personId);
        a.setType(type);
        a.setMessage(message);
        a.setLat(lat);
        a.setLon(lon);
        a.setStatus(AlertStatus.OPEN);
        a.setCreatedAt(now);
        a.setUpdatedAt(now);
        return repo.save(a);
    }

    @Override
    public Alert assign(String alertId, String helperId) {
        if (alertId == null || alertId.isBlank()) throw new IllegalArgumentException("alertId is required");
        if (helperId == null || helperId.isBlank()) throw new IllegalArgumentException("helperId is required");

        Alert a = repo.findById(alertId).orElseThrow(() -> new IllegalStateException("Alert not found"));
        if (a.getStatus() != AlertStatus.OPEN) {
            // Règle : on ne peut assigner qu’une alerte OPEN
            throw new IllegalStateException("Only OPEN can be ASSIGNED");
        }
        a.setHelperId(helperId);
        a.setStatus(AlertStatus.ASSIGNED);
        a.setUpdatedAt(Instant.now());
        return repo.save(a);
    }

    @Override
    public Alert resolve(String alertId, String helperId) {
        if (alertId == null || alertId.isBlank()) throw new IllegalArgumentException("alertId is required");
        if (helperId == null || helperId.isBlank()) throw new IllegalArgumentException("helperId is required");

        Alert a = repo.findById(alertId).orElseThrow(() -> new IllegalStateException("Alert not found"));
        if (a.getStatus() != AlertStatus.ASSIGNED) {
            // Règle : on ne peut résoudre qu’une alerte ASSIGNED
            throw new IllegalStateException("Only ASSIGNED can be RESOLVED");
        }
        if (a.getHelperId() == null || !a.getHelperId().equals(helperId)) {
            // Règle : seul l’aidant assigné peut résoudre
            throw new IllegalStateException("Only assigned helper can resolve");
        }
        a.setStatus(AlertStatus.RESOLVED);
        a.setResolvedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        return repo.save(a);
    }

    @Override
    public Alert getById(String alertId) {
        if (alertId == null || alertId.isBlank()) throw new IllegalArgumentException("alertId is required");
        return repo.findById(alertId).orElseThrow(() -> new IllegalStateException("Alert not found"));
    }

    @Override
    public List<Alert> listByPerson(String personId) {
        if (personId == null || personId.isBlank()) throw new IllegalArgumentException("personId is required");
        return repo.findByPersonId(personId);
    }

    @Override
    public List<Alert> listByHelper(String helperId) {
        if(helperId == null || helperId.isBlank()) throw new IllegalArgumentException("helperId is required");
        return repo.findByHelperId(helperId);
    }

    @Override
    public List<Alert> listByStatus(AlertStatus status) {
        if (status == null) throw new IllegalArgumentException("status is required");
        return repo.findByStatus(status);
    }
}