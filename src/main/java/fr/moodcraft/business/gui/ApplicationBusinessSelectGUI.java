package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessStatus;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class ApplicationBusinessSelectGUI {

    public static final String TITLE =
            "§8✦ §6Choisir Entreprise §8✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private ApplicationBusinessSelectGUI() {}

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

        List<Business> businesses =
                BusinessManager.getByStatus(
                        BusinessStatus.ACTIVE
                );

        int index = 0;

        for (Business business : businesses) {

            if (index >= SLOTS.length) {
                break;
            }

            if (business.isMember(
                    p.getUniqueId()
            )) {

                continue;
            }

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(Material.LIME_BANNER)
                            .name("§6✦ §f" + business.getName() + " §6✦")
                            .lore(
                                    "§7Dirigeant: §e" + business.getOwnerName(),
                                    "§7Statut: " + business.getStatus().getDisplayName(),
                                    "",
                                    "§eClique pour postuler"
                            )
                            .action("application_select_business")
                            .target(business.getId())
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
                                "§7Revenir aux candidatures."
                        )
                        .action("open_applications")
                        .build()
        );

        p.openInventory(inv);
    }
}