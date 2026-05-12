package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.RequestCategory;
import fr.moodcraft.business.model.RequestStatus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class RequestStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, BusinessRequest> requests =
            new LinkedHashMap<>();

    private RequestStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "requests.yml"
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

        requests.clear();

        ConfigurationSection section =
                config.getConfigurationSection("requests");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "requests." + id + ".";

            UUID creator;

            try {

                creator =
                        UUID.fromString(
                                config.getString(
                                        path + "creator.uuid",
                                        ""
                                )
                        );

            } catch (Exception e) {

                continue;
            }

            RequestCategory category =
                    parseCategory(
                            config.getString(
                                    path + "category",
                                    "AUTRE"
                            )
                    );

            RequestStatus status =
                    parseStatus(
                            config.getString(
                                    path + "status",
                                    "PUBLIEE"
                            )
                    );

            BusinessRequest request =
                    new BusinessRequest(
                            id,
                            creator,
                            config.getString(path + "creator.name", "Inconnu"),
                            config.getString(path + "title", "Sans titre"),
                            config.getString(path + "description", ""),
                            config.getDouble(path + "budget", 0),
                            config.getInt(path + "due-days", 7),
                            category,
                            status,
                            config.getLong(path + "created-at", System.currentTimeMillis()),
                            config.getLong(path + "updated-at", System.currentTimeMillis()),
                            config.getLong(path + "expires-at", 0),
                            config.getString(path + "accepted-offer-id", "")
                    );

            requests.put(
                    id,
                    request
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "requests",
                null
        );

        for (BusinessRequest request : requests.values()) {

            String path =
                    "requests."
                            + request.getId()
                            + ".";

            config.set(
                    path + "creator.uuid",
                    request.getCreatorUuid().toString()
            );

            config.set(
                    path + "creator.name",
                    request.getCreatorName()
            );

            config.set(
                    path + "title",
                    request.getTitle()
            );

            config.set(
                    path + "description",
                    request.getDescription()
            );

            config.set(
                    path + "budget",
                    request.getBudget()
            );

            config.set(
                    path + "due-days",
                    request.getDueDays()
            );

            config.set(
                    path + "category",
                    request.getCategory().name()
            );

            config.set(
                    path + "status",
                    request.getStatus().name()
            );

            config.set(
                    path + "created-at",
                    request.getCreatedAt()
            );

            config.set(
                    path + "updated-at",
                    request.getUpdatedAt()
            );

            config.set(
                    path + "expires-at",
                    request.getExpiresAt()
            );

            config.set(
                    path + "accepted-offer-id",
                    request.getAcceptedOfferId()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            BusinessRequest request
    ) {

        requests.put(
                request.getId(),
                request
        );

        save();
    }

    public static BusinessRequest get(
            String id
    ) {

        return requests.get(id);
    }

    public static Collection<BusinessRequest> getAll() {

        return requests.values();
    }

    private static RequestCategory parseCategory(
            String text
    ) {

        try {

            return RequestCategory.valueOf(text);

        } catch (Exception e) {

            return RequestCategory.AUTRE;
        }
    }

    private static RequestStatus parseStatus(
            String text
    ) {

        try {

            return RequestStatus.valueOf(text);

        } catch (Exception e) {

            return RequestStatus.PUBLIEE;
        }
    }
}