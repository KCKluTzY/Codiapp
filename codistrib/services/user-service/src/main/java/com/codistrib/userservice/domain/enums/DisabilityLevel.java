package com.codistrib.userservice.domain.enums;

public enum DisabilityLevel {
    MILD("Légère"),
    MODERATE("Modérée"),
    SEVERE("Sévère"),
    PROFOUND("Profonde");

    private final String displayName;

    DisabilityLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}