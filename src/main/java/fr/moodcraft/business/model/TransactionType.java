package fr.moodcraft.business.model;

public enum TransactionType {

    DEPOT(
            "§aDépôt"
    ),

    RETRAIT(
            "§cRetrait"
    ),

    PRIME(
            "§6Prime"
    ),

    PAIE_MENSUELLE(
            "§bPaie mensuelle"
    ),

    CONTRAT_VERSEMENT(
            "§eVersement contrat"
    ),

    TAXE(
            "§cTaxe"
    ),

    AJUSTEMENT(
            "§7Ajustement"
    );

    private final String displayName;

    TransactionType(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }
}