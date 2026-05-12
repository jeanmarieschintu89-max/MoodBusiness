package fr.moodcraft.business.model;

public enum OfferStatus {

    EN_ATTENTE(
            "§eEn attente"
    ),

    ACCEPTEE(
            "§aAcceptée"
    ),

    REFUSEE(
            "§cRefusée"
    ),

    ANNULEE(
            "§8Annulée"
    );

    private final String displayName;

    OfferStatus(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }

    public boolean isActive() {

        return this == EN_ATTENTE;
    }
}