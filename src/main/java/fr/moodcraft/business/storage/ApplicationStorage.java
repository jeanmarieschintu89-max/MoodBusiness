package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Application;
import fr.moodcraft.business.model.ApplicationStatus;
import fr.moodcraft.business.model.ApplicationType;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ApplicationStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Application> applications =
            new LinkedHashMap<>();

    private ApplicationStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "applications.yml"
                );

        if (!file.exists()) {

            try {

                file.getParentFile().mkdirs();
                file.createNewFile();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        config =
                YamlConfiguration.loadConfiguration(file);

        load();
        save();
    }

    public static void load() {

        applications.clear();

        ConfigurationSection section =
                config.getConfigurationSection("applications");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "applications." + id + ".";

            UUID applicant;

            try {

                applicant =
                        UUID.fromString(
                                config.getString(
                                        path + "applicant.uuid",
                                        ""
                                )
                        );

            } catch (Exception e) {

                continue;
            }

            ApplicationType type =
                    parseType(
                            config.getString(
                                    path + "type",
                                    "STAGE"
                            )
                    );

            ApplicationStatus status =
                    parseStatus(
                            config.getString(
                                    path + "status",
                                    "EN_ATTENTE"
                            )
                    );

            Application application =
                    new Application(
                            id,
                            config.getString(path + "business.id", ""),
                            config.getString(path + "business.name", "Inconnu"),
                            applicant,
                            config.getString(path + "applicant.name", "Inconnu"),
                            type,
                            status,
                            config.getString(path + "presentation", ""),
                            config.getString(path + "availability", ""),
                            config.getLong(path + "created-at", System.currentTimeMillis()),
                            config.getLong(path + "updated-at", System.currentTimeMillis()),
                            config.getLong(path + "expires-at", 0),
                            config.getString(path + "decision.by", ""),
                            config.getString(path + "decision.reason", "")
                    );

            applications.put(
                    id,
                    application
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "applications",
                null
        );

        for (Application application : applications.values()) {

            String path =
                    "applications."
                            + application.getId()
                            + ".";

            config.set(
                    path + "business.id",
                    application.getBusinessId()
            );

            config.set(
                    path + "business.name",
                    application.getBusinessName()
            );

            config.set(
                    path + "applicant.uuid",
                    application.getApplicantUuid().toString()
            );

            config.set(
                    path + "applicant.name",
                    application.getApplicantName()
            );

            config.set(
                    path + "type",
                    application.getType().name()
            );

            config.set(
                    path + "status",
                    application.getStatus().name()
            );

            config.set(
                    path + "presentation",
                    application.getPresentation()
            );

            config.set(
                    path + "availability",
                    application.getAvailability()
            );

            config.set(
                    path + "created-at",
                    application.getCreatedAt()
            );

            config.set(
                    path + "updated-at",
                    application.getUpdatedAt()
            );

            config.set(
                    path + "expires-at",
                    application.getExpiresAt()
            );

            config.set(
                    path + "decision.by",
                    application.getDecisionBy()
            );

            config.set(
                    path + "decision.reason",
                    application.getDecisionReason()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            Application application
    ) {

        applications.put(
                application.getId(),
                application
        );

        save();
    }

    public static Application get(
            String id
    ) {

        return applications.get(id);
    }

    public static Collection<Application> getAll() {

        return applications.values();
    }

    private static ApplicationType parseType(
            String text
    ) {

        try {

            return ApplicationType.valueOf(text);

        } catch (Exception e) {

            return ApplicationType.STAGE;
        }
    }

    private static ApplicationStatus parseStatus(
            String text
    ) {

        try {

            return ApplicationStatus.valueOf(text);

        } catch (Exception e) {

            return ApplicationStatus.EN_ATTENTE;
        }
    }
}