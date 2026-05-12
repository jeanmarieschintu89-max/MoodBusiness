package fr.moodcraft.business.gui;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessStaffGUI {

    public static final String TITLE =
            "§8✦ §6Gestion Entreprises §8✦";

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
                                "§7Consulter les entreprises",
                                "§7actuellement actives.",
                                "",
                                "§a✔ Registre ouvert"
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
                                "§7Voir les dernières créations.",
                                "§7Utile pour surveiller les abus.",
                                "",
                                "§eContrôle économique"
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
                                "§7Consulter ou réactiver les",
                                "§7entreprises sanctionnées.",
                                "",
                                "§cRegistre disciplinaire"
                        )
                        .action("staff_suspended")
                        .build()
        );

        SafeGUI.set(
                inv,
                25,
                new ItemBuilder(Material.ANVIL)
                        .name("§6✦ §fLitiges économiques §6✦")
                        .lore(
                                "§7Consulter les contrats en litige.",
                                "§7Les fonds restent bloqués",
                                "§7jusqu'à décision administrative.",
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
                        .name("§6✦ §fHistorique administratif §6✦")
                        .lore(
                                "§7Voir les dernières actions",
                                "§7du Registre Économique.",
                                "",
                                "§eAudit central"
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
                                "§7Revenir au registre principal."
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}