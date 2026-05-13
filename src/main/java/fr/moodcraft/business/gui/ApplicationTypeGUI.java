package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class ApplicationTypeGUI {

    public static final String TITLE =
            "§6✦ §8Type de Candidature §6✦";

    private ApplicationTypeGUI() {}

    public static void open(
            Player p,
            Business business
    ) {

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
                        .name("§6✦ §f" + shortText(business.getName(), 20) + " §6✦")
                        .lore(
                                "§7Choisis comment",
                                "§7tu veux rejoindre.",
                                "",
                                "§8• §7Stage",
                                "§8• §7Apprentissage"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fStage §6✦")
                        .lore(
                                "§7Découvrir l'entreprise.",
                                "",
                                "§8• §7Rôle très limité",
                                "§8• §7Idéal pour débuter",
                                "",
                                "§eClique pour continuer"
                        )
                        .action("application_start")
                        .target(business.getId() + ":STAGE")
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.COPPER_INGOT)
                        .name("§6✦ §fApprentissage §6✦")
                        .lore(
                                "§7Participer davantage.",
                                "",
                                "§8• §7Formation active",
                                "§8• §7Avec supervision",
                                "",
                                "§eClique pour continuer"
                        )
                        .action("application_start")
                        .target(business.getId() + ":APPRENTISSAGE")
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Choisir une entreprise"
                        )
                        .action("application_choose_business")
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
