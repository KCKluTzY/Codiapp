package com.codistrib.userservice.domain.enums;

public enum UserStatus {
    PENDING_CREDENTIALS("En attente de credentials"),
    ACTIVE("Actif"),
    SUSPENDED("Suspendu"),
    INACTIVE("Inactif");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}