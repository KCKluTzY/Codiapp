package com.codistrib.userservice.domain.enums;

public enum UserRole {
    PERSONNE_DI("Personne avec déficience intellectuelle"),
    HELPER("Aidant"),
    ADMIN("Administrateur");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}