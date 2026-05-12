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
                20,
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
                22,
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
                24,
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
                31,
                new ItemBuilder(Material.ANVIL)
                        .name("§6✦ §fLitiges économiques §6✦")
                        .lore(
                                "§7Les litiges arriveront avec",
                                "§7le module contrats sécurisés.",
                                "",
                                "§8• §7Bientôt disponible."
                        )
                        .action("coming_soon")
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