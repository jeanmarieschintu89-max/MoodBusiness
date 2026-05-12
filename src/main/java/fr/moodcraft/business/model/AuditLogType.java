package fr.moodcraft.business.model;

public enum AuditLogType {

    BUSINESS_CREATED(
            "§aEntreprise créée"
    ),

    BUSINESS_SUSPENDED(
            "§cEntreprise suspendue"
    ),

    BUSINESS_REACTIVATED(
            "§aEntreprise réactivée"
    ),

    MEMBER_ADDED(
            "§bMembre recruté"
    ),

    MEMBER_REMOVED(
            "§cMembre renvoyé"
    ),

    ROLE_CHANGED(
            "§eRôle modifié"
    ),

    APPLICATION_CREATED(
            "§bCandidature envoyée"
    ),

    APPLICATION_UPDATED(
            "§eCandidature traitée"
    ),

    REQUEST_CREATED(
            "§aDemande créée"
    ),

    OFFER_CREATED(
            "§eOffre envoyée"
    ),

    OFFER_ACCEPTED(
            "§6Offre acceptée"
    ),

    CONTRACT_CREATED(
            "§aContrat créé"
    ),

    CONTRACT_COMPLETED(
            "§eContrat terminé"
    ),

    CONTRACT_VALIDATED(
            "§6Contrat validé"
    ),

    CONTRACT_LITIGE(
            "§cLitige ouvert"
    ),

    BANK_DEPOSIT(
            "§aDépôt bancaire"
    ),

    BANK_WITHDRAW(
            "§cRetrait bancaire"
    ),

    BONUS_PAID(
            "§6Prime versée"
    ),

    PAYROLL_PAID(
            "§bPaie mensuelle"
    ),

    PAYROLL_BLOCKED(
            "§cPaie bloquée"
    ),

    STAFF_ACTION(
            "§cAction staff"
    ),

    SYSTEM(
            "§7Système"
    );

    private final String displayName;

    AuditLogType(
            String displayName
    ) {

        this.displayName = displayName;
    }

    public String getDisplayName() {

        return displayName;
    }
}