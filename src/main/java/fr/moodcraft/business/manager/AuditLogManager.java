package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.AuditLogEntry;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.storage.AuditLogStorage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AuditLogManager {

    private AuditLogManager() {}

    public static void log(
            AuditLogType type,
            String actorName,
            String targetName,
            Business business,
            String message
    ) {

        long now =
                System.currentTimeMillis();

        String id =
                now
                        + "-"
                        + type.name()
                        + "-"
                        + AuditLogStorage.getAll().size();

        AuditLogEntry entry =
                new AuditLogEntry(
                        id,
                        type,
                        actorName != null && !actorName.isBlank()
                                ? actorName
                                : "Système",
                        targetName != null
                                ? targetName
                                : "",
                        business != null
                                ? business.getId()
                                : "",
                        business != null
                                ? business.getName()
                                : "",
                        message != null
                                ? message
                                : "",
                        now
                );

        AuditLogStorage.add(entry);
    }

    public static void log(
            AuditLogType type,
            Player actor,
            String targetName,
            Business business,
            String message
    ) {

        log(
                type,
                actor != null
                        ? actor.getName()
                        : "Système",
                targetName,
                business,
                message
        );
    }

    public static void log(
            AuditLogType type,
            CommandSender actor,
            String targetName,
            Business business,
            String message
    ) {

        log(
                type,
                actor != null
                        ? actor.getName()
                        : "Système",
                targetName,
                business,
                message
        );
    }

    public static void system(
            String message
    ) {

        log(
                AuditLogType.SYSTEM,
                "Système",
                "",
                null,
                message
        );
    }
}