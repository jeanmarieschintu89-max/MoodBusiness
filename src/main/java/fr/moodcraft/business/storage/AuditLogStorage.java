package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.AuditLogEntry;
import fr.moodcraft.business.model.AuditLogType;

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

public final class AuditLogStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, AuditLogEntry> logs =
            new LinkedHashMap<>();

    private AuditLogStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "logs.yml"
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

        logs.clear();

        ConfigurationSection section =
                config.getConfigurationSection("logs");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "logs." + id + ".";

            AuditLogType type;

            try {

                type =
                        AuditLogType.valueOf(
                                config.getString(
                                        path + "type",
                                        "SYSTEM"
                                )
                        );

            } catch (Exception e) {

                type =
                        AuditLogType.SYSTEM;
            }

            AuditLogEntry entry =
                    new AuditLogEntry(
                            id,
                            type,
                            config.getString(path + "actor", "Système"),
                            config.getString(path + "target", ""),
                            config.getString(path + "business.id", ""),
                            config.getString(path + "business.name", ""),
                            config.getString(path + "message", ""),
                            config.getLong(path + "created-at", System.currentTimeMillis())
                    );

            logs.put(
                    id,
                    entry
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "logs",
                null
        );

        for (AuditLogEntry entry : logs.values()) {

            String path =
                    "logs."
                            + entry.getId()
                            + ".";

            config.set(
                    path + "type",
                    entry.getType().name()
            );

            config.set(
                    path + "actor",
                    entry.getActorName()
            );

            config.set(
                    path + "target",
                    entry.getTargetName()
            );

            config.set(
                    path + "business.id",
                    entry.getBusinessId()
            );

            config.set(
                    path + "business.name",
                    entry.getBusinessName()
            );

            config.set(
                    path + "message",
                    entry.getMessage()
            );

            config.set(
                    path + "created-at",
                    entry.getCreatedAt()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            AuditLogEntry entry
    ) {

        logs.put(
                entry.getId(),
                entry
        );

        save();
    }

    public static Collection<AuditLogEntry> getAll() {

        return logs.values();
    }

    public static List<AuditLogEntry> getRecent(
            int limit
    ) {

        List<AuditLogEntry> list =
                new ArrayList<>(
                        logs.values()
                );

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

    public static List<AuditLogEntry> getByBusiness(
            String businessId,
            int limit
    ) {

        List<AuditLogEntry> list =
                new ArrayList<>();

        for (AuditLogEntry entry : logs.values()) {

            if (entry.getBusinessId()
                    .equalsIgnoreCase(businessId)) {

                list.add(entry);
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