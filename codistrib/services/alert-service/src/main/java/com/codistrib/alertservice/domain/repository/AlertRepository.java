package com.codistrib.alertservice.domain.repository;

import com.codistrib.alertservice.domain.enums.AlertStatus;
import com.codistrib.alertservice.domain.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert> findByPersonId(String personId);
    List<Alert> findByStatus(AlertStatus status);
    List<Alert> findByHelperId(String helperId);
}