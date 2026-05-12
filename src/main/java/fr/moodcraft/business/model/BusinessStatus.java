package fr.moodcraft.business.model;

public enum BusinessStatus {

    ACTIVE(
            "§aActive"
    ),

    SUSPENDUE(
            "§cSuspendue"
    ),

    ARCHIVEE(
            "§8Archivée"
    );

    private final String displayName;

    BusinessStatus(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }
}