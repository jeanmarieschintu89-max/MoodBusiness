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

    public static final String TITLE = GuiTitle.of("Choisir Entreprise");

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private ApplicationBusinessSelectGUI() {}

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        SafeGUI.fill(inv);

        List<Business> businesses = BusinessManager.getByStatus(BusinessStatus.ACTIVE);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fChoisir une entreprise §6✦")
                        .lore(
                                "§8• §7Clique une entreprise",
                                "§8• §7pour envoyer une candidature",
                                "",
                                "§8• §7Stage",
                                "§8• §7Apprentissage"
                        )
                        .build()
        );

        int index = 0;

        for (Business business : businesses) {
            if (index >= SLOTS.length) break;
            if (business.isMember(p.getUniqueId())) continue;

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(Material.LIME_BANNER)
                            .name("§6✦ §f" + shortText(business.getName(), 18) + " §6✦")
                            .lore(
                                    "§8• §7Dirigeant : §e" + shortText(business.getOwnerName(), 16),
                                    "§8• §7État : " + business.getStatus().getDisplayName(),
                                    "",
                                    "§8• §7Postuler ici",
                                    "§8• §7Choix du type après",
                                    "",
                                    "§e➜ §fChoisir"
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
                        .name("§6✦ §fRetour §6✦")
                        .lore("§8• §7Menu candidatures")
                        .action("open_applications")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
