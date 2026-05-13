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
            "§8✦ §6Contrats §8✦";

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
                                "§7Voir vos contrats",
                                "§7personnels.",
                                "",
                                "§8• §7En cours",
                                "§8• §7Terminés",
                                "§8• §7Litiges",
                                "",
                                "§7Total: §e"
                                        + ContractManager.getByClient(p).size(),
                                "",
                                "§eClique pour ouvrir"
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
                                    "§7Voir les contrats",
                                    "§7de votre entreprise.",
                                    "",
                                    "§7Entreprise: §e" + shortText(business.getName(), 18),
                                    "§7Total: §e"
                                            + ContractManager.getByBusiness(business).size(),
                                    "",
                                    "§8• §7Travail à faire",
                                    "§8• §7Paiements à valider",
                                    "",
                                    "§eClique pour ouvrir"
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
                        .name("§6✦ §fArgent bloqué §6✦")
                        .lore(
                                "§7Quand une offre est acceptée,",
                                "§7l'argent est gardé de côté.",
                                "",
                                "§8• §7Le client paie avant",
                                "§8• §7L'entreprise reçoit après",
                                "§8• §7Taxe: §c20%",
                                "",
                                "§7Cela protège les deux joueurs."
                        )
                        .build()
        );

        if (p.hasPermission("moodbusiness.staff.litige")) {

            SafeGUI.set(
                    inv,
                    31,
                    new ItemBuilder(Material.ANVIL)
                            .name("§6✦ §fLitiges §6✦")
                            .lore(
                                    "§7Voir les contrats",
                                    "§7avec un problème.",
                                    "",
                                    "§8• §7Analyser le litige",
                                    "§8• §7Payer l'entreprise",
                                    "§8• §7Rembourser le client",
                                    "",
                                    "§cAccès staff"
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
                                "§7Bureau des Entreprises"
                        )
                        .action("back_main")
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