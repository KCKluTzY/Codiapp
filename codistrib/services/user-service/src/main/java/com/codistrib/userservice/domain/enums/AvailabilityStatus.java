package com.codistrib.userservice.domain.enums;

public enum AvailabilityStatus {
    AVAILABLE("Disponible"),
    BUSY("Occupé"),
    OFFLINE("Hors ligne");

    private final String displayName;

    AvailabilityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}