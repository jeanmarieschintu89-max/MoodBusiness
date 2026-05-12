package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Alert;
import fr.moodcraft.business.model.AlertType;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AlertStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Alert> alerts =
            new LinkedHashMap<>();

    private AlertStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "alerts.yml"
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

        alerts.clear();

        ConfigurationSection section =
                config.getConfigurationSection("alerts");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "alerts." + id + ".";

            UUID playerUuid;

            try {

                playerUuid =
                        UUID.fromString(
                                config.getString(
                                        path + "player.uuid",
                                        ""
                                )
                        );

            } catch (Exception e) {

                continue;
            }

            AlertType type;

            try {

                type =
                        AlertType.valueOf(
                                config.getString(
                                        path + "type",
                                        "SYSTEM"
                                )
                        );

            } catch (Exception e) {

                type =
                        AlertType.SYSTEM;
            }

            Alert alert =
                    new Alert(
                            id,
                            playerUuid,
                            config.getString(path + "player.name", "Inconnu"),
                            type,
                            config.getString(path + "title", "Notification"),
                            config.getString(path + "message", ""),
                            config.getLong(path + "created-at", System.currentTimeMillis()),
                            config.getBoolean(path + "read", false)
                    );

            alerts.put(
                    id,
                    alert
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "alerts",
                null
        );

        for (Alert alert : alerts.values()) {

            String path =
                    "alerts."
                            + alert.getId()
                            + ".";

            config.set(
                    path + "player.uuid",
                    alert.getPlayerUuid().toString()
            );

            config.set(
                    path + "player.name",
                    alert.getPlayerName()
            );

            config.set(
                    path + "type",
                    alert.getType().name()
            );

            config.set(
                    path + "title",
                    alert.getTitle()
            );

            config.set(
                    path + "message",
                    alert.getMessage()
            );

            config.set(
                    path + "created-at",
                    alert.getCreatedAt()
            );

            config.set(
                    path + "read",
                    alert.isRead()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            Alert alert
    ) {

        alerts.put(
                alert.getId(),
                alert
        );

        save();
    }

    public static Alert get(
            String id
    ) {

        return alerts.get(id);
    }

    public static Collection<Alert> getAll() {

        return alerts.values();
    }

    public static List<Alert> getUnread(
            UUID uuid
    ) {

        List<Alert> list =
                new ArrayList<>();

        for (Alert alert : alerts.values()) {

            if (alert.getPlayerUuid().equals(uuid)
                    && !alert.isRead()) {

                list.add(alert);
            }
        }

        list.sort(
                (a, b) ->
                        Long.compare(
                                b.getCreatedAt(),
                                a.getCreatedAt()
                        )
        );

        return list;
    }

    public static List<Alert> getAllFor(
            UUID uuid,
            int limit
    ) {

        List<Alert> list =
                new ArrayList<>();

        for (Alert alert : alerts.values()) {

            if (alert.getPlayerUuid().equals(uuid)) {

                list.add(alert);
            }
        }

        list.sort(
                (a, b) ->
                        Long.compare(
                                b.getCreatedAt(),
                                a.getCreatedAt()
                        )
        );

        if (list.size() <= limit) {
            return list;
        }

        return list.subList(
                0,
                limit
        );
    }
}