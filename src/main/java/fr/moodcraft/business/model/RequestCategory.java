package fr.moodcraft.business.model;

public enum RequestCategory {

    CONSTRUCTION("§6Construction"),
    RESSOURCES("§bRessources"),
    SERVICE("§fService"),
    AUTRE("§8Autre");

    private final String displayName;

    RequestCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
