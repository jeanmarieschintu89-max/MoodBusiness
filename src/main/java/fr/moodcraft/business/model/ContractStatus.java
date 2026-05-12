package fr.moodcraft.business.model;

public enum ContractStatus {

    EN_COURS(
            "§aEn cours"
    ),

    EN_RETARD(
            "§cEn retard"
    ),

    TERMINE(
            "§eTerminé"
    ),

    VALIDE(
            "§6Validé"
    ),

    LITIGE(
            "§cLitige"
    ),

    ANNULE(
            "§8Annulé"
    );

    private final String displayName;

    ContractStatus(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }

    public boolean isOpen() {

        return this == EN_COURS
                || this == EN_RETARD
                || this == TERMINE
                || this == LITIGE;
    }
}