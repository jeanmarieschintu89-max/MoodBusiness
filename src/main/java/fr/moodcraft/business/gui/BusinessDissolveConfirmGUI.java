package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessDissolveConfirmGUI {

    public static final String TITLE =
            "§8✦ §cDissolution Entreprise §8✦";

    private BusinessDissolveConfirmGUI() {}

    public static void open(
            Player p,
            Business business
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        27,
                        TITLE
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BARRIER)
                        .name("§c✦ Dissoudre l’entreprise §c✦")
                        .lore(
                                "§7Entreprise: §e" + business.getName(),
                                "§7Dirigeant: §e" + business.getOwnerName(),
                                "§7Solde banque: §e" + VaultHook.format(business.getBalance()),
                                "",
                                "§cCette action archive l’entreprise.",
                                "§7Elle ne sera plus active.",
                                "",
                                "§8• §7Les logs seront conservés",
                                "§8• §7Les contrats doivent être clôturés",
                                "§8• §7La banque doit être vide",
                                "§8• §7La prochaine création coûtera plus cher",
                                "",
                                "§cAction sensible"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                11,
                new ItemBuilder(Material.REDSTONE_BLOCK)
                        .name("§c✘ Annuler")
                        .lore(
                                "§7Revenir à la gestion",
                                "§7de l’entreprise.",
                                "",
                                "§c▶ Annuler"
                        )
                        .action("dissolve_cancel")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                15,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name("§c✔ Confirmer la dissolution")
                        .lore(
                                "§7Archive définitivement",
                                "§7l’entreprise active.",
                                "",
                                "§c▶ Dissoudre"
                        )
                        .action("dissolve_confirm")
                        .target(business.getId())
                        .build()
        );

        p.openInventory(inv);
    }
}