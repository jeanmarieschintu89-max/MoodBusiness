package fr.moodcraft.business.model;

public enum ApplicationStatus {

    EN_ATTENTE(
            "§eEn attente"
    ),

    ENTRETIEN(
            "§bEntretien demandé"
    ),

    ACCEPTEE_STAGE(
            "§aAcceptée comme stagiaire"
    ),

    ACCEPTEE_APPRENTISSAGE(
            "§aAcceptée comme apprenti"
    ),

    REFUSEE(
            "§cRefusée"
    ),

    ANNULEE(
            "§8Annulée"
    ),

    EXPIREE(
            "§7Expirée"
    );

    private final String displayName;

    ApplicationStatus(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }

    public boolean isActive() {

        return this == EN_ATTENTE
                || this == ENTRETIEN;
    }
}