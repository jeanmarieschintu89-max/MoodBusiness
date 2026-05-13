package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.ContractAssignmentManager;

import fr.moodcraft.business.model.ContractAssignment;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class MyMissionsGUI {

    public static final String TITLE =
            "§6✦ §8Mes Missions §6✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private MyMissionsGUI() {}

    public static void open(
            Player p
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        List<ContractAssignment> missions =
                ContractAssignmentManager.getByMember(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMes missions §6✦")
                        .lore(
                                "§7Contrats où vous",
                                "§7êtes assigné.",
                                "",
                                "§7Missions: §e" + missions.size(),
                                "",
                                "§8• §7Travail à suivre",
                                "§8• §7Entreprise liée"
                        )
                        .build()
        );

        int index = 0;

        for (ContractAssignment mission : missions) {

            if (index >= SLOTS.length) {
                break;
            }

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name("§6✦ §f" + shortText(mission.getContractTitle(), 20) + " §6✦")
                            .lore(
                                    "§7Entreprise: §e" + shortText(mission.getBusinessName(), 18),
                                    "§7Rôle: " + mission.getMemberRole().getDisplayName(),
                                    "§7Assigné par: §e" + shortText(mission.getAssignedByName(), 16),
                                    "§7Date: §f" + shortDate(mission.getAssignedAt()),
                                    "",
                                    "§8• §7Mission active"
                            )
                            .action("contract_detail")
                            .target(mission.getContractId())
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
                                "§7Menu contrats"
                        )
                        .action("open_contracts")
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

    private static String shortDate(
            long time
    ) {

        String date =
                TimeUtil.formatDate(time);

        if (date == null || date.equalsIgnoreCase("Jamais")) {
            return "Aucune";
        }

        if (date.length() <= 10) {
            return date;
        }

        return date.substring(0, 10);
    }
}