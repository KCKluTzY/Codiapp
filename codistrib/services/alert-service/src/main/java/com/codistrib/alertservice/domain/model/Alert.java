package com.codistrib.alertservice.domain.model;

import com.codistrib.alertservice.domain.enums.AlertStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "alerts")
public class Alert {
    @Id
    private String id;
    private String personId;     // DI déclencheur
    private String helperId;     // aidant/responsable assigné
    private String type;         // SOS, INCIDENT, QUESTION
    private String message;      // description courte
    private Double lat;          // optionnel
    private Double lon;          // optionnel
    private AlertStatus status;  // OPEN, ASSIGNED, RESOLVED, CANCELED
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;  // null si non résolue

    public Alert() {}
    public Alert(String personId, String helperId, String type, String message) {
        this.personId = personId;
        this.helperId = helperId;
        this.type = type;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}