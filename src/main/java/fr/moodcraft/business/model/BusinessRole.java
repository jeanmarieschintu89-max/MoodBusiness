package fr.moodcraft.business.model;

import java.util.Locale;

public enum BusinessRole {

    DIRIGEANT(
            "§6Dirigeant",
            100
    ),

    GERANT(
            "§eGérant",
            80
    ),

    RESPONSABLE_CONTRATS(
            "§bResponsable contrats",
            60
    ),

    TRESORIER(
            "§aTrésorier",
            50
    ),

    EMPLOYE(
            "§fEmployé",
            30
    ),

    APPRENTI(
            "§eApprenti",
            20
    ),

    STAGIAIRE(
            "§7Stagiaire",
            10
    );

    private final String displayName;
    private final int power;

    BusinessRole(
            String displayName,
            int power
    ) {

        this.displayName = displayName;
        this.power = power;
    }

    public String getDisplayName() {

        return displayName;
    }

    public int getPower() {

        return power;
    }

    public boolean isAtLeast(
            BusinessRole other
    ) {

        return power >= other.power;
    }

    public boolean canManageRoles() {

        return this == DIRIGEANT
                || this == GERANT;
    }

    public boolean canManageBank() {

        return this == DIRIGEANT
                || this == GERANT
                || this == TRESORIER;
    }

    public boolean canManageContracts() {

        return this == DIRIGEANT
                || this == GERANT
                || this == RESPONSABLE_CONTRATS;
    }

    public boolean isTrainingRole() {

        return this == APPRENTI
                || this == STAGIAIRE;
    }

    public static BusinessRole fromText(
            String text
    ) {

        if (text == null) {
            return null;
        }

        String clean =
                text.toLowerCase(Locale.ROOT)
                        .replace("é", "e")
                        .replace("è", "e")
                        .replace("ê", "e")
                        .replace(" ", "_")
                        .replace("-", "_");

        return switch (clean) {

            case "dirigeant", "owner", "chef" ->
                    DIRIGEANT;

            case "gerant", "gérant", "manager" ->
                    GERANT;

            case "responsable", "responsable_contrats", "contrats" ->
                    RESPONSABLE_CONTRATS;

            case "tresorier", "trésorier", "banque" ->
                    TRESORIER;

            case "employe", "employé", "employee" ->
                    EMPLOYE;

            case "apprenti", "apprentissage" ->
                    APPRENTI;

            case "stagiaire", "stage" ->
                    STAGIAIRE;

            default ->
                    null;
        };
    }
}