package fr.moodcraft.business.model;

public enum RequestStatus {

    PUBLIEE(
            "§aPubliée"
    ),

    OFFRES_RECUES(
            "§eOffres reçues"
    ),

    TRANSFORMEE_CONTRAT(
            "§6Transformée en contrat"
    ),

    ANNULEE(
            "§8Annulée"
    ),

    EXPIREE(
            "§7Expirée"
    );

    private final String displayName;

    RequestStatus(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }

    public boolean isOpen() {

        return this == PUBLIEE
                || this == OFFRES_RECUES;
    }
}