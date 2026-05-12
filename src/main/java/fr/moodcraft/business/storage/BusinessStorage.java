package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.BusinessStatus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class BusinessStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Business> businesses =
            new LinkedHashMap<>();

    private static final Map<UUID, Integer> createdCounts =
            new LinkedHashMap<>();

    private static final Map<UUID, Long> cooldownUntil =
            new LinkedHashMap<>();

    private static final Map<UUID, Boolean> registerSuspended =
            new LinkedHashMap<>();

    private BusinessStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "businesses.yml"
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

        businesses.clear();
        createdCounts.clear();
        cooldownUntil.clear();
        registerSuspended.clear();

        ConfigurationSection businessSection =
                config.getConfigurationSection("businesses");

        if (businessSection != null) {

            for (String id : businessSection.getKeys(false)) {

                String path =
                        "businesses." + id + ".";

                String name =
                        config.getString(
                                path + "name",
                                id
                        );

                UUID owner;

                try {

                    owner =
                            UUID.fromString(
                                    config.getString(
                                            path + "owner.uuid",
                                            ""
                                    )
                            );

                } catch (Exception e) {

                    continue;
                }

                String ownerName =
                        config.getString(
                                path + "owner.name",
                                "Inconnu"
                        );

                BusinessStatus status =
                        parseStatus(
                                config.getString(
                                        path + "status",
                                        "ACTIVE"
                                )
                        );

                double balance =
                        config.getDouble(
                                path + "balance",
                                0
                        );

                int creationIndex =
                        config.getInt(
                                path + "creation.index",
                                1
                        );

                double creationFee =
                        config.getDouble(
                                path + "creation.fee",
                                0
                        );

                long createdAt =
                        config.getLong(
                                path + "created-at",
                                System.currentTimeMillis()
                        );

                long updatedAt =
                        config.getLong(
                                path + "updated-at",
                                createdAt
                        );

                Business business =
                        new Business(
                                id,
                                name,
                                owner,
                                ownerName,
                                status,
                                balance,
                                creationIndex,
                                creationFee,
                                createdAt
                        );

                business.getMembers().clear();
                business.getMemberNames().clear();

                ConfigurationSection members =
                        config.getConfigurationSection(
                                path + "members"
                        );

                if (members != null) {

                    for (String uuidText : members.getKeys(false)) {

                        try {

                            UUID uuid =
                                    UUID.fromString(uuidText);

                            String memberPath =
                                    path
                                            + "members."
                                            + uuidText;

                            BusinessRole role;
                            String memberName;

                            if (config.isConfigurationSection(memberPath)) {

                                role =
                                        parseRole(
                                                config.getString(
                                                        memberPath + ".role",
                                                        "EMPLOYE"
                                                )
                                        );

                                memberName =
                                        config.getString(
                                                memberPath + ".name",
                                                "Inconnu"
                                        );

                            } else {

                                role =
                                        parseRole(
                                                config.getString(
                                                        memberPath,
                                                        "EMPLOYE"
                                                )
                                        );

                                memberName =
                                        uuid.equals(owner)
                                                ? ownerName
                                                : "Inconnu";
                            }

                            business.addMember(
                                    uuid,
                                    memberName,
                                    role
                            );

                        } catch (Exception ignored) {}
                    }
                }

                if (!business.getMembers().containsKey(owner)) {

                    business.addMember(
                            owner,
                            ownerName,
                            BusinessRole.DIRIGEANT
                    );
                }

                business.setUpdatedAt(updatedAt);

                businesses.put(
                        id,
                        business
                );
            }
        }

        ConfigurationSection counts =
                config.getConfigurationSection("created-counts");

        if (counts != null) {

            for (String uuidText : counts.getKeys(false)) {

                try {

                    createdCounts.put(
                            UUID.fromString(uuidText),
                            counts.getInt(uuidText)
                    );

                } catch (Exception ignored) {}
            }
        }

        ConfigurationSection cooldowns =
                config.getConfigurationSection("cooldown-until");

        if (cooldowns != null) {

            for (String uuidText : cooldowns.getKeys(false)) {

                try {

                    cooldownUntil.put(
                            UUID.fromString(uuidText),
                            cooldowns.getLong(uuidText)
                    );

                } catch (Exception ignored) {}
            }
        }

        ConfigurationSection suspended =
                config.getConfigurationSection("register-suspended");

        if (suspended != null) {

            for (String uuidText : suspended.getKeys(false)) {

                try {

                    registerSuspended.put(
                            UUID.fromString(uuidText),
                            suspended.getBoolean(uuidText)
                    );

                } catch (Exception ignored) {}
            }
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set("businesses", null);
        config.set("created-counts", null);
        config.set("cooldown-until", null);
        config.set("register-suspended", null);

        for (Business business : businesses.values()) {

            String path =
                    "businesses."
                            + business.getId()
                            + ".";

            config.set(
                    path + "name",
                    business.getName()
            );

            config.set(
                    path + "owner.uuid",
                    business.getOwnerUuid().toString()
            );

            config.set(
                    path + "owner.name",
                    business.getOwnerName()
            );

            config.set(
                    path + "status",
                    business.getStatus().name()
            );

            config.set(
                    path + "balance",
                    business.getBalance()
            );

            config.set(
                    path + "creation.index",
                    business.getCreationIndex()
            );

            config.set(
                    path + "creation.fee",
                    business.getCreationFee()
            );

            config.set(
                    path + "created-at",
                    business.getCreatedAt()
            );

            config.set(
                    path + "updated-at",
                    business.getUpdatedAt()
            );

            config.set(
                    path + "members",
                    null
            );

            for (Map.Entry<UUID, BusinessRole> entry :
                    business.getMembers().entrySet()) {

                UUID uuid =
                        entry.getKey();

                config.set(
                        path
                                + "members."
                                + uuid
                                + ".name",
                        business.getMemberName(uuid)
                );

                config.set(
                        path
                                + "members."
                                + uuid
                                + ".role",
                        entry.getValue().name()
                );
            }
        }

        for (Map.Entry<UUID, Integer> entry :
                createdCounts.entrySet()) {

            config.set(
                    "created-counts."
                            + entry.getKey(),
                    entry.getValue()
            );
        }

        for (Map.Entry<UUID, Long> entry :
                cooldownUntil.entrySet()) {

            config.set(
                    "cooldown-until."
                            + entry.getKey(),
                    entry.getValue()
            );
        }

        for (Map.Entry<UUID, Boolean> entry :
                registerSuspended.entrySet()) {

            config.set(
                    "register-suspended."
                            + entry.getKey(),
                    entry.getValue()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void addBusiness(
            Business business
    ) {

        businesses.put(
                business.getId(),
                business
        );

        save();
    }

    public static Business getBusiness(
            String id
    ) {

        return businesses.get(id);
    }

    public static boolean exists(
            String id
    ) {

        return businesses.containsKey(id);
    }

    public static Collection<Business> getBusinesses() {

        return businesses.values();
    }

    public static int getCreatedCount(
            UUID uuid
    ) {

        return createdCounts.getOrDefault(
                uuid,
                0
        );
    }

    public static void setCreatedCount(
            UUID uuid,
            int count
    ) {

        createdCounts.put(
                uuid,
                count
        );

        save();
    }

    public static long getCooldownUntil(
            UUID uuid
    ) {

        return cooldownUntil.getOrDefault(
                uuid,
                0L
        );
    }

    public static void setCooldownUntil(
            UUID uuid,
            long time
    ) {

        cooldownUntil.put(
                uuid,
                time
        );

        save();
    }

    public static boolean isRegisterSuspended(
            UUID uuid
    ) {

        return registerSuspended.getOrDefault(
                uuid,
                false
        );
    }

    public static void setRegisterSuspended(
            UUID uuid,
            boolean value
    ) {

        registerSuspended.put(
                uuid,
                value
        );

        save();
    }

    private static BusinessStatus parseStatus(
            String text
    ) {

        try {

            return BusinessStatus.valueOf(text);

        } catch (Exception e) {

            return BusinessStatus.ACTIVE;
        }
    }

    private static BusinessRole parseRole(
            String text
    ) {

        try {

            return BusinessRole.valueOf(text);

        } catch (Exception e) {

            return BusinessRole.EMPLOYE;
        }
    }
}