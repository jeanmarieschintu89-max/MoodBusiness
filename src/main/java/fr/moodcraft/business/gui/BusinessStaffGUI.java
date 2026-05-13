package fr.moodcraft.business.gui;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessStaffGUI {

    public static final String TITLE =
            "§6✦ §8Gestion Entreprises §6✦";

    private BusinessStaffGUI() {}

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

        SafeGUI.set(
                inv,
                19,
                new ItemBuilder(Material.LIME_BANNER)
                        .name("§6✦ §fEntreprises actives §6✦")
                        .lore(
                                "§7Voir les entreprises",
                                "§7ouvertes sur le serveur.",
                                "",
                                "§8• §7Infos",
                                "§8• §7Gestion",
                                "§8• §7Suspension",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("staff_active")
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.CLOCK)
                        .name("§6✦ §fEntreprises récentes §6✦")
                        .lore(
                                "§7Voir les dernières",
                                "§7entreprises créées.",
                                "",
                                "§8• §7Surveiller les abus",
                                "§8• §7Vérifier les noms",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("staff_recent")
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.RED_BANNER)
                        .name("§6✦ §fEntreprises suspendues §6✦")
                        .lore(
                                "§7Voir les entreprises",
                                "§7bloquées par le staff.",
                                "",
                                "§8• §7Consulter",
                                "§8• §7Réactiver",
                                "§8• §7Archiver",
                                "",
                                "§cAccès staff"
                        )
                        .action("staff_suspended")
                        .build()
        );

        SafeGUI.set(
                inv,
                25,
                new ItemBuilder(Material.ANVIL)
                        .name("§6✦ §fLitiges §6✦")
                        .lore(
                                "§7Voir les contrats",
                                "§7avec un problème.",
                                "",
                                "§8• §7Argent bloqué",
                                "§8• §7Analyse staff",
                                "§8• §7Décision finale",
                                "",
                                "§cAccès staff"
                        )
                        .action("contract_litige_list")
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fLogs §6✦")
                        .lore(
                                "§7Voir les dernières",
                                "§7actions importantes.",
                                "",
                                "§8• §7Entreprises",
                                "§8• §7Contrats",
                                "§8• §7Banque",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("audit_logs")
                        .build()
        );

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Bureau des Entreprises"
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}
