package fr.moodcraft.business.storage;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.ContractAssignment;

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

public final class ContractAssignmentStorage {

    private static File file;
    private static FileConfiguration config;

    private static final Map<String, ContractAssignment> assignments =
            new LinkedHashMap<>();

    private ContractAssignmentStorage() {}

    public static void init() {

        file =
                new File(
                        Main.getInstance().getDataFolder(),
                        "missions.yml"
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

        assignments.clear();

        ConfigurationSection section =
                config.getConfigurationSection("missions");

        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {

            String path =
                    "missions." + id + ".";

            UUID memberUuid;
            UUID assignedByUuid;

            try {

                memberUuid =
                        UUID.fromString(
                                config.getString(
                                        path + "member.uuid",
                                        ""
                                )
                        );

                assignedByUuid =
                        UUID.fromString(
                                config.getString(
                                        path + "assigned-by.uuid",
                                        ""
                                )
                        );

            } catch (Exception e) {

                continue;
            }

            BusinessRole role;

            try {

                role =
                        BusinessRole.valueOf(
                                config.getString(
                                        path + "member.role",
                                        "EMPLOYE"
                                )
                        );

            } catch (Exception e) {

                role =
                        BusinessRole.EMPLOYE;
            }

            ContractAssignment assignment =
                    new ContractAssignment(
                            id,
                            config.getString(path + "contract.id", ""),
                            config.getString(path + "contract.title", "Contrat"),
                            config.getString(path + "business.id", ""),
                            config.getString(path + "business.name", "Inconnu"),
                            memberUuid,
                            config.getString(path + "member.name", "Inconnu"),
                            role,
                            assignedByUuid,
                            config.getString(path + "assigned-by.name", "Inconnu"),
                            config.getLong(path + "assigned-at", System.currentTimeMillis()),
                            config.getBoolean(path + "active", true)
                    );

            assignments.put(
                    id,
                    assignment
            );
        }
    }

    public static void save() {

        if (config == null || file == null) {
            return;
        }

        config.set(
                "missions",
                null
        );

        for (ContractAssignment assignment :
                assignments.values()) {

            String path =
                    "missions."
                            + assignment.getId()
                            + ".";

            config.set(
                    path + "contract.id",
                    assignment.getContractId()
            );

            config.set(
                    path + "contract.title",
                    assignment.getContractTitle()
            );

            config.set(
                    path + "business.id",
                    assignment.getBusinessId()
            );

            config.set(
                    path + "business.name",
                    assignment.getBusinessName()
            );

            config.set(
                    path + "member.uuid",
                    assignment.getMemberUuid().toString()
            );

            config.set(
                    path + "member.name",
                    assignment.getMemberName()
            );

            config.set(
                    path + "member.role",
                    assignment.getMemberRole().name()
            );

            config.set(
                    path + "assigned-by.uuid",
                    assignment.getAssignedByUuid().toString()
            );

            config.set(
                    path + "assigned-by.name",
                    assignment.getAssignedByName()
            );

            config.set(
                    path + "assigned-at",
                    assignment.getAssignedAt()
            );

            config.set(
                    path + "active",
                    assignment.isActive()
            );
        }

        try {

            config.save(file);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static void add(
            ContractAssignment assignment
    ) {

        assignments.put(
                assignment.getId(),
                assignment
        );

        save();
    }

    public static ContractAssignment get(
            String id
    ) {

        return assignments.get(id);
    }

    public static Collection<ContractAssignment> getAll() {

        return assignments.values();
    }

    public static List<ContractAssignment> getByContract(
            String contractId
    ) {

        List<ContractAssignment> list =
                new ArrayList<>();

        for (ContractAssignment assignment :
                assignments.values()) {

            if (assignment.getContractId().equalsIgnoreCase(contractId)) {

                list.add(assignment);
            }
        }

        list.sort(
                (a, b) -> Long.compare(
                        b.getAssignedAt(),
                        a.getAssignedAt()
                )
        );

        return list;
    }

    public static List<ContractAssignment> getByMember(
            UUID uuid
    ) {

        List<ContractAssignment> list =
                new ArrayList<>();

        for (ContractAssignment assignment :
                assignments.values()) {

            if (assignment.getMemberUuid().equals(uuid)
                    && assignment.isActive()) {

                list.add(assignment);
            }
        }

        list.sort(
                (a, b) -> Long.compare(
                        b.getAssignedAt(),
                        a.getAssignedAt()
                )
        );

        return list;
    }

    public static boolean hasActiveAssignment(
            String contractId,
            UUID memberUuid
    ) {

        for (ContractAssignment assignment :
                assignments.values()) {

            if (assignment.getContractId().equalsIgnoreCase(contractId)
                    && assignment.getMemberUuid().equals(memberUuid)
                    && assignment.isActive()) {

                return true;
            }
        }

        return false;
    }
}