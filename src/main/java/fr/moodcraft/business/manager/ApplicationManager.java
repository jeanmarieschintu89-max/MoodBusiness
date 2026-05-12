package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.Application;
import fr.moodcraft.business.model.ApplicationStatus;
import fr.moodcraft.business.model.ApplicationType;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.storage.ApplicationStorage;
import fr.moodcraft.business.storage.BusinessStorage;

import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class ApplicationManager {

    private ApplicationManager() {}

    public static ApplicationResult createApplication(
            Player player,
            Business business,
            ApplicationType type,
            String presentation,
            String availability
    ) {

        if (player == null || business == null || type == null) {

            return ApplicationResult.fail(
                    "Dossier de candidature invalide."
            );
        }

        if (!business.isActive()) {

            return ApplicationResult.fail(
                    "Cette entreprise n'est pas active."
            );
        }

        if (business.isMember(
                player.getUniqueId()
        )) {

            return ApplicationResult.fail(
                    "Vous faites déjà partie de cette entreprise."
            );
        }

        int maxActive =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "applications.max-active-per-player",
                                3
                        );

        if (countActiveByApplicant(
                player.getUniqueId()
        ) >= maxActive) {

            return ApplicationResult.fail(
                    "Vous avez déjà trop de candidatures actives."
            );
        }

        if (hasActiveForBusiness(
                player.getUniqueId(),
                business.getId()
        )) {

            return ApplicationResult.fail(
                    "Vous avez déjà une candidature active pour cette entreprise."
            );
        }

        long now =
                System.currentTimeMillis();

        int expireDays =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "applications.expire-after-days",
                                7
                        );

        String id =
                now
                        + "-"
                        + player.getUniqueId()
                        .toString()
                        .substring(0, 8);

        Application application =
                new Application(
                        id,
                        business.getId(),
                        business.getName(),
                        player.getUniqueId(),
                        player.getName(),
                        type,
                        ApplicationStatus.EN_ATTENTE,
                        presentation,
                        availability,
                        now,
                        now,
                        now + TimeUtil.days(expireDays),
                        "",
                        ""
                );

        ApplicationStorage.add(application);

        AuditLogManager.log(
                AuditLogType.APPLICATION_CREATED,
                player,
                business.getName(),
                business,
                "Candidature envoyée: "
                        + type.getDisplayName()
        );

        AlertManager.add(
                business.getOwnerUuid(),
                business.getOwnerName(),
                AlertType.APPLICATION,
                "Nouvelle candidature",
                player.getName()
                        + " a envoyé une candidature pour "
                        + business.getName()
                        + "."
        );

        return ApplicationResult.success(
                application,
                "Candidature envoyée."
        );
    }

    public static int countActiveByApplicant(
            UUID uuid
    ) {

        int count = 0;

        for (Application application :
                ApplicationStorage.getAll()) {

            if (application.getApplicantUuid().equals(uuid)
                    && application.getStatus().isActive()
                    && !application.isExpired()) {

                count++;
            }
        }

        return count;
    }

    public static boolean hasActiveForBusiness(
            UUID uuid,
            String businessId
    ) {

        for (Application application :
                ApplicationStorage.getAll()) {

            if (application.getApplicantUuid().equals(uuid)
                    && application.getBusinessId().equalsIgnoreCase(businessId)
                    && application.getStatus().isActive()
                    && !application.isExpired()) {

                return true;
            }
        }

        return false;
    }

    public static List<Application> getByApplicant(
            UUID uuid
    ) {

        List<Application> list =
                new ArrayList<>();

        for (Application application :
                ApplicationStorage.getAll()) {

            if (application.getApplicantUuid().equals(uuid)) {

                refreshExpiration(application);
                list.add(application);
            }
        }

        sort(list);

        return list;
    }

    public static List<Application> getByBusiness(
            String businessId
    ) {

        List<Application> list =
                new ArrayList<>();

        for (Application application :
                ApplicationStorage.getAll()) {

            if (application.getBusinessId().equalsIgnoreCase(businessId)) {

                refreshExpiration(application);
                list.add(application);
            }
        }

        sort(list);

        return list;
    }

    public static List<Application> getPendingByBusiness(
            String businessId
    ) {

        List<Application> list =
                new ArrayList<>();

        for (Application application :
                getByBusiness(businessId)) {

            if (application.getStatus() == ApplicationStatus.EN_ATTENTE
                    || application.getStatus() == ApplicationStatus.ENTRETIEN) {

                list.add(application);
            }
        }

        sort(list);

        return list;
    }

    public static Application get(
            String id
    ) {

        Application application =
                ApplicationStorage.get(id);

        if (application != null) {

            refreshExpiration(application);
        }

        return application;
    }

    public static ApplicationResult requestInterview(
            Player actor,
            Application application
    ) {

        if (!canManageApplication(
                actor,
                application
        )) {

            return ApplicationResult.fail(
                    "Vous ne pouvez pas gérer cette candidature."
            );
        }

        if (!application.getStatus().isActive()) {

            return ApplicationResult.fail(
                    "Cette candidature n'est plus active."
            );
        }

        Business business =
                BusinessManager.getById(
                        application.getBusinessId()
                );

        application.setStatus(
                ApplicationStatus.ENTRETIEN
        );

        application.setDecisionBy(
                actor.getName()
        );

        application.setDecisionReason(
                "Entretien demandé"
        );

        ApplicationStorage.save();

        AuditLogManager.log(
                AuditLogType.APPLICATION_UPDATED,
                actor,
                application.getApplicantName(),
                business,
                "Entretien demandé pour une candidature."
        );

        OfflinePlayer applicant =
                Bukkit.getOfflinePlayer(
                        application.getApplicantUuid()
                );

        AlertManager.add(
                applicant,
                AlertType.APPLICATION,
                "Entretien demandé",
                business != null
                        ? "L'entreprise " + business.getName() + " souhaite un entretien."
                        : "Une entreprise souhaite un entretien."
        );

        return ApplicationResult.success(
                application,
                "Entretien demandé."
        );
    }

    public static ApplicationResult refuse(
            Player actor,
            Application application,
            String reason
    ) {

        if (!canManageApplication(
                actor,
                application
        )) {

            return ApplicationResult.fail(
                    "Vous ne pouvez pas gérer cette candidature."
            );
        }

        if (!application.getStatus().isActive()) {

            return ApplicationResult.fail(
                    "Cette candidature n'est plus active."
            );
        }

        Business business =
                BusinessManager.getById(
                        application.getBusinessId()
                );

        application.setStatus(
                ApplicationStatus.REFUSEE
        );

        application.setDecisionBy(
                actor.getName()
        );

        application.setDecisionReason(
                reason != null && !reason.isBlank()
                        ? reason
                        : "Refusée par l'entreprise"
        );

        ApplicationStorage.save();

        AuditLogManager.log(
                AuditLogType.APPLICATION_UPDATED,
                actor,
                application.getApplicantName(),
                business,
                "Candidature refusée. Raison: "
                        + application.getDecisionReason()
        );

        OfflinePlayer applicant =
                Bukkit.getOfflinePlayer(
                        application.getApplicantUuid()
                );

        AlertManager.add(
                applicant,
                AlertType.APPLICATION,
                "Candidature refusée",
                business != null
                        ? "Votre candidature chez " + business.getName() + " a été refusée."
                        : "Votre candidature a été refusée."
        );

        return ApplicationResult.success(
                application,
                "Candidature refusée."
        );
    }

    public static ApplicationResult accept(
            Player actor,
            Application application,
            BusinessRole role
    ) {

        if (!canManageApplication(
                actor,
                application
        )) {

            return ApplicationResult.fail(
                    "Vous ne pouvez pas gérer cette candidature."
            );
        }

        if (!application.getStatus().isActive()) {

            return ApplicationResult.fail(
                    "Cette candidature n'est plus active."
            );
        }

        Business business =
                BusinessManager.getById(
                        application.getBusinessId()
                );

        if (business == null) {

            return ApplicationResult.fail(
                    "Entreprise introuvable."
            );
        }

        if (business.isMember(
                application.getApplicantUuid()
        )) {

            return ApplicationResult.fail(
                    "Ce joueur est déjà dans l'entreprise."
            );
        }

        if (role != BusinessRole.STAGIAIRE
                && role != BusinessRole.APPRENTI) {

            return ApplicationResult.fail(
                    "Cette candidature ne peut donner qu'un rôle de formation."
            );
        }

        business.addMember(
                application.getApplicantUuid(),
                application.getApplicantName(),
                role
        );

        if (role == BusinessRole.STAGIAIRE) {

            application.setStatus(
                    ApplicationStatus.ACCEPTEE_STAGE
            );

        } else {

            application.setStatus(
                    ApplicationStatus.ACCEPTEE_APPRENTISSAGE
            );
        }

        application.setDecisionBy(
                actor.getName()
        );

        application.setDecisionReason(
                "Acceptée dans l'entreprise"
        );

        BusinessStorage.save();
        ApplicationStorage.save();

        AuditLogManager.log(
                AuditLogType.APPLICATION_UPDATED,
                actor,
                application.getApplicantName(),
                business,
                "Candidature acceptée avec rôle: "
                        + role.getDisplayName()
        );

        AuditLogManager.log(
                AuditLogType.MEMBER_ADDED,
                actor,
                application.getApplicantName(),
                business,
                "Membre ajouté depuis une candidature avec rôle: "
                        + role.getDisplayName()
        );

        OfflinePlayer applicant =
                Bukkit.getOfflinePlayer(
                        application.getApplicantUuid()
                );

        AlertManager.add(
                applicant,
                AlertType.APPLICATION,
                "Candidature acceptée",
                "Vous avez rejoint "
                        + business.getName()
                        + " avec le rôle "
                        + role.getDisplayName()
                        + "."
        );

        return ApplicationResult.success(
                application,
                "Candidature acceptée."
        );
    }

    public static boolean canManageApplication(
            Player actor,
            Application application
    ) {

        if (actor == null || application == null) {
            return false;
        }

        Business business =
                BusinessManager.getById(
                        application.getBusinessId()
                );

        if (business == null) {
            return false;
        }

        return BusinessManager.canManageRoles(
                actor,
                business
        );
    }

    private static void refreshExpiration(
            Application application
    ) {

        if (application == null) {
            return;
        }

        if (application.getStatus().isActive()
                && application.isExpired()) {

            application.setStatus(
                    ApplicationStatus.EXPIREE
            );

            application.setDecisionReason(
                    "Expiration automatique"
            );

            ApplicationStorage.save();
        }
    }

    private static void sort(
            List<Application> list
    ) {

        list.sort(
                Comparator.comparingLong(
                        Application::getCreatedAt
                ).reversed()
        );
    }

    public record ApplicationResult(
            boolean success,
            Application application,
            String message
    ) {

        public static ApplicationResult success(
                Application application,
                String message
        ) {

            return new ApplicationResult(
                    true,
                    application,
                    message
            );
        }

        public static ApplicationResult fail(
                String message
        ) {

            return new ApplicationResult(
                    false,
                    null,
                    message
            );
        }
    }
}