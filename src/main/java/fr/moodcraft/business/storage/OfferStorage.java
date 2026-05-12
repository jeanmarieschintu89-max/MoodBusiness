package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Offer;
import fr.moodcraft.business.model.OfferStatus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class OfferStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Offer> offers =
            new LinkedHashMap<>();

    private OfferStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "offers.yml"
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

        offers.clear();

        ConfigurationSection section =
                config.getConfigurationSection("offers");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "offers." + id + ".";

            UUID sender;

            try {

                sender =
                        UUID.fromString(
                                config.getString(
                                        path + "sender.uuid",
                                        ""
                                )
                        );

            } catch (Exception e) {

                continue;
            }

            OfferStatus status =
                    parseStatus(
                            config.getString(
                                    path + "status",
                                    "EN_ATTENTE"
                            )
                    );

            Offer offer =
                    new Offer(
                            id,
                            config.getString(path + "request-id", ""),
                            config.getString(path + "business.id", ""),
                            config.getString(path + "business.name", "Inconnu"),
                            sender,
                            config.getString(path + "sender.name", "Inconnu"),
                            config.getDouble(path + "amount", 0),
                            config.getInt(path + "due-days", 7),
                            config.getString(path + "comment", ""),
                            status,
                            config.getLong(path + "created-at", System.currentTimeMillis()),
                            config.getLong(path + "updated-at", System.currentTimeMillis())
                    );

            offers.put(
                    id,
                    offer
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "offers",
                null
        );

        for (Offer offer : offers.values()) {

            String path =
                    "offers."
                            + offer.getId()
                            + ".";

            config.set(
                    path + "request-id",
                    offer.getRequestId()
            );

            config.set(
                    path + "business.id",
                    offer.getBusinessId()
            );

            config.set(
                    path + "business.name",
                    offer.getBusinessName()
            );

            config.set(
                    path + "sender.uuid",
                    offer.getSenderUuid().toString()
            );

            config.set(
                    path + "sender.name",
                    offer.getSenderName()
            );

            config.set(
                    path + "amount",
                    offer.getAmount()
            );

            config.set(
                    path + "due-days",
                    offer.getDueDays()
            );

            config.set(
                    path + "comment",
                    offer.getComment()
            );

            config.set(
                    path + "status",
                    offer.getStatus().name()
            );

            config.set(
                    path + "created-at",
                    offer.getCreatedAt()
            );

            config.set(
                    path + "updated-at",
                    offer.getUpdatedAt()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            Offer offer
    ) {

        offers.put(
                offer.getId(),
                offer
        );

        save();
    }

    public static Offer get(
            String id
    ) {

        return offers.get(id);
    }

    public static Collection<Offer> getAll() {

        return offers.values();
    }

    private static OfferStatus parseStatus(
            String text
    ) {

        try {

            return OfferStatus.valueOf(text);

        } catch (Exception e) {

            return OfferStatus.EN_ATTENTE;
        }
    }
}