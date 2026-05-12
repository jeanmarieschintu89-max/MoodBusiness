package fr.moodcraft.business.model;

public enum ApplicationType {

    STAGE(
            "§bStage",
            BusinessRole.STAGIAIRE
    ),

    APPRENTISSAGE(
            "§eApprentissage",
            BusinessRole.APPRENTI
    );

    private final String displayName;
    private final BusinessRole targetRole;

    ApplicationType(
            String displayName,
            BusinessRole targetRole
    ) {

        this.displayName = displayName;
        this.targetRole = targetRole;
    }

    public String getDisplayName() {

        return displayName;
    }

    public BusinessRole getTargetRole() {

        return targetRole;
    }
}