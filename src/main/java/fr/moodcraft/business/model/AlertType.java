package fr.moodcraft.business.model;

public enum AlertType {

    BUSINESS(
            "§6Entreprise"
    ),

    APPLICATION(
            "§bCandidature"
    ),

    REQUEST(
            "§eDemande"
    ),

    OFFER(
            "§aOffre"
    ),

    CONTRACT(
            "§6Contrat"
    ),

    BANK(
            "§eBanque"
    ),

    PAYROLL(
            "§bPaie"
    ),

    LITIGE(
            "§cLitige"
    ),

    STAFF(
            "§cAdministration"
    ),

    SYSTEM(
            "§7Système"
    );

    private final String displayName;

    AlertType(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }
}