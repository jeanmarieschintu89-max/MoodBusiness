package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class ContractMainGUI {

    public static final String TITLE =
            "§8✦ §6Contrats Officiels §8✦";

    private ContractMainGUI() {}

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
                13,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMes contrats §6✦")
                        .lore(
                                "§7Consulter vos contrats personnels.",
                                "§7Fonds bloqués, validations",
                                "§7et litiges en cours.",
                                "",
                                "§7Total: §e"
                                        + ContractManager.getByClient(p).size(),
                                "",
                                "§a✔ Ouvrir"
                        )
                        .action("contract_my_list")
                        .build()
        );

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        if (business != null) {

            SafeGUI.set(
                    inv,
                    21,
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name("§6✦ §fContrats entreprise §6✦")
                            .lore(
                                    "§7Contrats liés à votre entreprise.",
                                    "§7Entreprise: §e" + business.getName(),
                                    "",
                                    "§7Total: §e"
                                            + ContractManager.getByBusiness(business).size(),
                                    "",
                                    "§a✔ Ouvrir"
                            )
                            .action("contract_business_list")
                            .target(business.getId())
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§6✦ §fSécurité des fonds §6✦")
                        .lore(
                                "§7Quand une offre est acceptée,",
                                "§7l'argent est immédiatement bloqué.",
                                "",
                                "§7L'entreprise reçoit le paiement",
                                "§7uniquement après validation.",
                                "",
                                "§cTaxe économique: §f20%"
                        )
                        .build()
        );

        if (p.hasPermission("moodbusiness.staff.litige")) {

            SafeGUI.set(
                    inv,
                    31,
                    new ItemBuilder(Material.ANVIL)
                            .name("§6✦ §fLitiges économiques §6✦")
                            .lore(
                                    "§7Consulter les contrats en litige.",
                                    "",
                                    "§cAccès administratif"
                            )
                            .action("contract_litige_list")
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                49,
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