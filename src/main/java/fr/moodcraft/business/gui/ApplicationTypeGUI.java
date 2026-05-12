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
            "§8✦ §6Type de Candidature §8✦";

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
                        .name("§6✦ §f" + business.getName() + " §6✦")
                        .lore(
                                "§7Choisissez le type de candidature.",
                                "§8• §7Service officiel de §aMood§6Craft§7."
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fStage §6✦")
                        .lore(
                                "§7Découvrir l'entreprise",
                                "§7sans accès sensible.",
                                "",
                                "§b✔ Formation légère"
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
                                "§7Participer à l'activité",
                                "§7sous supervision.",
                                "",
                                "§e✔ Parcours encadré"
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
                                "§7Choisir une autre entreprise."
                        )
                        .action("application_choose_business")
                        .build()
        );

        p.openInventory(inv);
    }
}