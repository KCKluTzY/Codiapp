package com.codistrib.userservice.domain.enums;

public enum ValidationStatus {
    PENDING("En attente"),
    APPROVED("Approuvé"),
    REJECTED("Rejeté");

    private final String displayName;

    ValidationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}