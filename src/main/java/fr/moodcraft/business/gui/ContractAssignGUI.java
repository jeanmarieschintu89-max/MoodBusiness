package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractAssignmentManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ContractAssignGUI {

    public static final String TITLE =
            "§6✦ §8Assigner Mission §6✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private ContractAssignGUI() {}

    public static void open(
            Player p,
            Contract contract
    ) {

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        if (business == null) {
            return;
        }

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §f" + shortText(contract.getTitle(), 22) + " §6✦")
                        .lore(
                                "§7Choisis un membre",
                                "§7pour ce contrat.",
                                "",
                                "§7Entreprise: §e" + shortText(business.getName(), 18),
                                "",
                                "§8• §7Employé",
                                "§8• §7Apprenti",
                                "§8• §7Stagiaire"
                        )
                        .build()
        );

        List<Map.Entry<UUID, BusinessRole>> members =
                new ArrayList<>(
                        business.getMembers().entrySet()
                );

        int index = 0;

        for (Map.Entry<UUID, BusinessRole> entry : members) {

            if (index >= SLOTS.length) {
                break;
            }

            UUID uuid =
                    entry.getKey();

            BusinessRole role =
                    entry.getValue();

            if (role == BusinessRole.DIRIGEANT) {
                continue;
            }

            boolean already =
                    ContractAssignmentManager.getByContract(
                            contract.getId()
                    ).stream().anyMatch(
                            assignment -> assignment.getMemberUuid().equals(uuid)
                                    && assignment.isActive()
                    );

            Material icon =
                    switch (role) {

                        case GERANT -> Material.GOLDEN_HELMET;
                        case RESPONSABLE_CONTRATS -> Material.WRITABLE_BOOK;
                        case TRESORIER -> Material.GOLD_INGOT;
                        case EMPLOYE -> Material.IRON_PICKAXE;
                        case APPRENTI -> Material.COPPER_INGOT;
                        case STAGIAIRE -> Material.PAPER;
                        case DIRIGEANT -> Material.NETHER_STAR;
                    };

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(
                            already
                                    ? Material.GRAY_DYE
                                    : icon
                    )
                            .name("§6✦ §f" + shortText(business.getMemberName(uuid), 18) + " §6✦")
                            .lore(
                                    "§7Rôle: " + role.getDisplayName(),
                                    "",
                                    already
                                            ? "§cDéjà assigné"
                                            : "§a✔ Assigner",
                                    "",
                                    "§8• §7Mission liée au contrat"
                            )
                            .action(
                                    already
                                            ? "coming_soon"
                                            : "contract_assign_member"
                            )
                            .target(contract.getId() + ":" + uuid)
                            .build()
            );

            index++;
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Dossier du contrat"
                        )
                        .action("contract_detail")
                        .target(contract.getId())
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(
                0,
                Math.max(1, max - 3)
        ) + "...";
    }
}