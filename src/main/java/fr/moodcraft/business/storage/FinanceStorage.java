package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.FinanceTransaction;
import fr.moodcraft.business.model.TransactionType;

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

public final class FinanceStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, FinanceTransaction> transactions =
            new LinkedHashMap<>();

    private FinanceStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "finance.yml"
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

        transactions.clear();

        ConfigurationSection section =
                config.getConfigurationSection("transactions");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "transactions." + id + ".";

            TransactionType type;

            try {

                type =
                        TransactionType.valueOf(
                                config.getString(
                                        path + "type",
                                        "AJUSTEMENT"
                                )
                        );

            } catch (Exception e) {

                type =
                        TransactionType.AJUSTEMENT;
            }

            FinanceTransaction transaction =
                    new FinanceTransaction(
                            id,
                            config.getString(path + "business.id", ""),
                            config.getString(path + "business.name", "Inconnu"),
                            type,
                            config.getDouble(path + "amount", 0),
                            config.getString(path + "actor", "Système"),
                            config.getString(path + "target", ""),
                            config.getString(path + "note", ""),
                            config.getLong(path + "created-at", System.currentTimeMillis())
                    );

            transactions.put(
                    id,
                    transaction
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "transactions",
                null
        );

        for (FinanceTransaction transaction : transactions.values()) {

            String path =
                    "transactions."
                            + transaction.getId()
                            + ".";

            config.set(
                    path + "business.id",
                    transaction.getBusinessId()
            );

            config.set(
                    path + "business.name",
                    transaction.getBusinessName()
            );

            config.set(
                    path + "type",
                    transaction.getType().name()
            );

            config.set(
                    path + "amount",
                    transaction.getAmount()
            );

            config.set(
                    path + "actor",
                    transaction.getActorName()
            );

            config.set(
                    path + "target",
                    transaction.getTargetName()
            );

            config.set(
                    path + "note",
                    transaction.getNote()
            );

            config.set(
                    path + "created-at",
                    transaction.getCreatedAt()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            Business business,
            TransactionType type,
            double amount,
            String actorName,
            String targetName,
            String note
    ) {

        long now =
                System.currentTimeMillis();

        String id =
                now
                        + "-"
                        + business.getId()
                        + "-"
                        + transactions.size();

        FinanceTransaction transaction =
                new FinanceTransaction(
                        id,
                        business.getId(),
                        business.getName(),
                        type,
                        amount,
                        actorName != null
                                ? actorName
                                : "Système",
                        targetName != null
                                ? targetName
                                : "",
                        note != null
                                ? note
                                : "",
                        now
                );

        transactions.put(
                id,
                transaction
        );

        save();
    }

    public static List<FinanceTransaction> getByBusiness(
            String businessId
    ) {

        List<FinanceTransaction> list =
                new ArrayList<>();

        for (FinanceTransaction transaction :
                transactions.values()) {

            if (transaction.getBusinessId()
                    .equalsIgnoreCase(businessId)) {

                list.add(transaction);
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

    public static Collection<FinanceTransaction> getAll() {

        return transactions.values();
    }
}