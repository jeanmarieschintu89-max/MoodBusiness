package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessAdminManageGUI {

    public static final String TITLE =
            "§8✦ §6Admin Entreprise §8✦";

    private BusinessAdminManageGUI() {}

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
                new ItemBuilder(Material.NETHER_STAR)
                        .name("§6✦ §f" + business.getName() + " §6✦")
                        .lore(
                                "§7Dossier administratif.",
                                "",
                                "§7Dirigeant: §e" + business.getOwnerName(),
                                "§7Statut: " + business.getStatus().getDisplayName(),
                                "§7Solde entreprise: §e" + VaultHook.format(business.getBalance()),
                                "",
                                "§8• §7ID: §8" + business.getId(),
                                "§8• §7Service officiel de §aMood§6Craft§7."
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.LIME_BANNER)
                        .name("§a✦ Réactiver")
                        .lore(
                                "§7Réactive cette entreprise",
                                "§7si elle est suspendue.",
                                "",
                                "§a▶ Réactiver"
                        )
                        .action("admin_reactivate_business")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.RED_BANNER)
                        .name("§c✦ Suspendre")
                        .lore(
                                "§7Bloque temporairement",
                                "§7cette entreprise.",
                                "",
                                "§c▶ Suspendre"
                        )
                        .action("admin_suspend_business")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name("§c✦ Dissoudre / Archiver")
                        .lore(
                                "§7Archive cette entreprise.",
                                "§7Elle ne sera plus active.",
                                "",
                                "§8• §7Banque vide requise",
                                "§8• §7Contrats ouverts interdits",
                                "§8• §7Historique conservé",
                                "",
                                "§cAction sensible"
                        )
                        .action("admin_dissolve_business")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fVoir le dossier §6✦")
                        .lore(
                                "§7Afficher les informations",
                                "§7dans le chat.",
                                "",
                                "§e▶ Consulter"
                        )
                        .action("admin_info_business")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Retour à la gestion staff."
                        )
                        .action("open_staff")
                        .build()
        );

        p.openInventory(inv);
    }
}