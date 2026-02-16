package com.codistrib.userservice.domain.enums;

public enum CredentialRequestStatus {
    PENDING("En attente"),
    APPROVED("Approuvée"),
    REJECTED("Rejetée");

    private final String displayName;

    CredentialRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}