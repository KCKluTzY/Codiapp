package com.codistrib.userservice.domain.enums;

public enum PreferredCommunication {
    TEXT("Texte"),
    PICTOGRAM("Pictogrammes"),
    VOICE("Voix"),
    VIDEO("Vidéo");

    private final String displayName;

    PreferredCommunication(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}