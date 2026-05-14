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
                        45,
                        TITLE
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.LIME_BANNER)
                        .name("§6✦ §fActives §6✦")
                        .lore(
                                "§8• §7Entreprises ouvertes",
                                "§8• §7Infos et gestion",
                                "§8• §7Suspension possible",
                                "",
                                "§e➜ §fOuvrir"
                        )
                        .action("staff_active")
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.RED_BANNER)
                        .name("§6✦ §fSuspendues §6✦")
                        .lore(
                                "§8• §7Entreprises bloquées",
                                "§8• §7Réactiver ou archiver",
                                "§8• §7Contrôle staff",
                                "",
                                "§c✖ §fAccès staff"
                        )
                        .action("staff_suspended")
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.ANVIL)
                        .name("§6✦ §fLitiges §6✦")
                        .lore(
                                "§8• §7Contrats signalés",
                                "§8• §7Argent bloqué",
                                "§8• §7Décision administrative",
                                "",
                                "§c✖ §fAccès staff"
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
                                "§8• §7Actions importantes",
                                "§8• §7Entreprises",
                                "§8• §7Contrats et banque",
                                "",
                                "§e➜ §fOuvrir les logs"
                        )
                        .action("audit_logs")
                        .build()
        );

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.ARROW)
                        .name("§6✦ §fRetour §6✦")
                        .lore(
                                "§8• §7Bureau des Entreprises"
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}
