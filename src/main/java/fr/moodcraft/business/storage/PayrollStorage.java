package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.BusinessRole;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class PayrollStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, Map<BusinessRole, Double>> salaries =
            new HashMap<>();

    private static final Map<String, String> lastPaidMonth =
            new HashMap<>();

    private PayrollStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "payroll.yml"
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

        salaries.clear();
        lastPaidMonth.clear();

        ConfigurationSection salarySection =
                config.getConfigurationSection("salaries");

        if (salarySection != null) {

            for (String businessId : salarySection.getKeys(false)) {

                Map<BusinessRole, Double> map =
                        new EnumMap<>(BusinessRole.class);

                for (BusinessRole role : BusinessRole.values()) {

                    if (config.contains(
                            "salaries."
                                    + businessId
                                    + "."
                                    + role.name()
                    )) {

                        map.put(
                                role,
                                config.getDouble(
                                        "salaries."
                                                + businessId
                                                + "."
                                                + role.name(),
                                        0
                                )
                        );
                    }
                }

                salaries.put(
                        businessId,
                        map
                );
            }
        }

        ConfigurationSection paidSection =
                config.getConfigurationSection("last-paid-month");

        if (paidSection != null) {

            for (String businessId : paidSection.getKeys(false)) {

                lastPaidMonth.put(
                        businessId,
                        paidSection.getString(
                                businessId,
                                ""
                        )
                );
            }
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set("salaries", null);
        config.set("last-paid-month", null);

        for (Map.Entry<String, Map<BusinessRole, Double>> entry :
                salaries.entrySet()) {

            for (Map.Entry<BusinessRole, Double> salary :
                    entry.getValue().entrySet()) {

                config.set(
                        "salaries."
                                + entry.getKey()
                                + "."
                                + salary.getKey().name(),
                        salary.getValue()
                );
            }
        }

        for (Map.Entry<String, String> entry :
                lastPaidMonth.entrySet()) {

            config.set(
                    "last-paid-month."
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

    public static double getSalary(
            String businessId,
            BusinessRole role
    ) {

        Map<BusinessRole, Double> map =
                salaries.get(businessId);

        if (map != null && map.containsKey(role)) {

            return map.get(role);
        }

        return Main.getInstance()
                .getConfig()
                .getDouble(
                        "payroll.default-salaries."
                                + role.name(),
                        0
                );
    }

    public static void setSalary(
            String businessId,
            BusinessRole role,
            double amount
    ) {

        salaries.computeIfAbsent(
                businessId,
                ignored -> new EnumMap<>(BusinessRole.class)
        ).put(
                role,
                Math.max(
                        0,
                        amount
                )
        );

        save();
    }

    public static String getLastPaidMonth(
            String businessId
    ) {

        return lastPaidMonth.getOrDefault(
                businessId,
                ""
        );
    }

    public static void setLastPaidMonth(
            String businessId,
            String month
    ) {

        lastPaidMonth.put(
                businessId,
                month
        );

        save();
    }
}