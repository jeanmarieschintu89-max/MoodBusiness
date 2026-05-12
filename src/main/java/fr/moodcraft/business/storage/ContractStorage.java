package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class ContractStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Contract> contracts =
            new LinkedHashMap<>();

    private ContractStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "contracts.yml"
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

        contracts.clear();

        ConfigurationSection section =
                config.getConfigurationSection("contracts");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "contracts." + id + ".";

            UUID client;
            UUID businessActor;

            try {

                client =
                        UUID.fromString(
                                config.getString(
                                        path + "client.uuid",
                                        ""
                                )
                        );

                businessActor =
                        UUID.fromString(
                                config.getString(
                                        path + "business-actor.uuid",
                                        ""
                                )
                        );

            } catch (Exception e) {

                continue;
            }

            ContractStatus status =
                    parseStatus(
                            config.getString(
                                    path + "status",
                                    "EN_COURS"
                            )
                    );

            Contract contract =
                    new Contract(
                            id,
                            config.getString(path + "request-id", ""),
                            config.getString(path + "offer-id", ""),
                            client,
                            config.getString(path + "client.name", "Inconnu"),
                            config.getString(path + "business.id", ""),
                            config.getString(path + "business.name", "Inconnu"),
                            businessActor,
                            config.getString(path + "business-actor.name", "Inconnu"),
                            config.getString(path + "title", "Contrat"),
                            config.getString(path + "description", ""),
                            config.getDouble(path + "gross-amount", 0),
                            config.getDouble(path + "tax-rate", 20),
                            config.getDouble(path + "tax-amount", 0),
                            config.getDouble(path + "net-amount", 0),
                            config.getDouble(path + "escrow-amount", 0),
                            config.getInt(path + "due-days", 7),
                            status,
                            config.getLong(path + "created-at", System.currentTimeMillis()),
                            config.getLong(path + "updated-at", System.currentTimeMillis()),
                            config.getLong(path + "due-at", 0),
                            config.getLong(path + "completed-at", 0),
                            config.getLong(path + "validate-before", 0),
                            new ArrayList<>(
                                    config.getStringList(
                                            path + "history"
                                    )
                            )
                    );

            contracts.put(
                    id,
                    contract
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "contracts",
                null
        );

        for (Contract contract : contracts.values()) {

            String path =
                    "contracts."
                            + contract.getId()
                            + ".";

            config.set(
                    path + "request-id",
                    contract.getRequestId()
            );

            config.set(
                    path + "offer-id",
                    contract.getOfferId()
            );

            config.set(
                    path + "client.uuid",
                    contract.getClientUuid().toString()
            );

            config.set(
                    path + "client.name",
                    contract.getClientName()
            );

            config.set(
                    path + "business.id",
                    contract.getBusinessId()
            );

            config.set(
                    path + "business.name",
                    contract.getBusinessName()
            );

            config.set(
                    path + "business-actor.uuid",
                    contract.getBusinessActorUuid().toString()
            );

            config.set(
                    path + "business-actor.name",
                    contract.getBusinessActorName()
            );

            config.set(
                    path + "title",
                    contract.getTitle()
            );

            config.set(
                    path + "description",
                    contract.getDescription()
            );

            config.set(
                    path + "gross-amount",
                    contract.getGrossAmount()
            );

            config.set(
                    path + "tax-rate",
                    contract.getTaxRate()
            );

            config.set(
                    path + "tax-amount",
                    contract.getTaxAmount()
            );

            config.set(
                    path + "net-amount",
                    contract.getNetAmount()
            );

            config.set(
                    path + "escrow-amount",
                    contract.getEscrowAmount()
            );

            config.set(
                    path + "due-days",
                    contract.getDueDays()
            );

            config.set(
                    path + "status",
                    contract.getStatus().name()
            );

            config.set(
                    path + "created-at",
                    contract.getCreatedAt()
            );

            config.set(
                    path + "updated-at",
                    contract.getUpdatedAt()
            );

            config.set(
                    path + "due-at",
                    contract.getDueAt()
            );

            config.set(
                    path + "completed-at",
                    contract.getCompletedAt()
            );

            config.set(
                    path + "validate-before",
                    contract.getValidateBefore()
            );

            config.set(
                    path + "history",
                    contract.getHistory()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            Contract contract
    ) {

        contracts.put(
                contract.getId(),
                contract
        );

        save();
    }

    public static Contract get(
            String id
    ) {

        return contracts.get(id);
    }

    public static Collection<Contract> getAll() {

        return contracts.values();
    }

    private static ContractStatus parseStatus(
            String text
    ) {

        try {

            return ContractStatus.valueOf(text);

        } catch (Exception e) {

            return ContractStatus.EN_COURS;
        }
    }
}