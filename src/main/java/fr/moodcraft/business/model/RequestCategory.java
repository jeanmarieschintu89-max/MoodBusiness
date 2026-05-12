package fr.moodcraft.business.model;

public enum RequestCategory {

    CONSTRUCTION(
            "§6Construction"
    ),

    LIVRAISON(
            "§bLivraison"
    ),

    AGRICULTURE(
            "§aAgriculture"
    ),

    MINAGE(
            "§7Récolte / minage"
    ),

    COMMERCE(
            "§eCommerce"
    ),

    IMMOBILIER(
            "§dImmobilier"
    ),

    EVENEMENT(
            "§5Événement"
    ),

    SERVICE(
            "§fService"
    ),

    AUTRE(
            "§8Autre"
    );

    private final String displayName;

    RequestCategory(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }
}