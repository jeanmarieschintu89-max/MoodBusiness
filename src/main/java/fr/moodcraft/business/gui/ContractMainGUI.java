package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractAssignmentManager;
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
            "§6✦ §8Contrats §6✦";

    private ContractMainGUI() {}

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

        int myContracts =
                ContractManager.getByClient(p).size();

        int myMissions =
                ContractAssignmentManager.getByMember(
                        p.getUniqueId()
                ).size();

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fContrats §6✦")
                        .lore(
                                "§8• §7Paiement sécurisé",
                                "§8• §7Argent bloqué",
                                "§8• §7Taxe 20%",
                                "§8• §7Litiges possibles",
                                "",
                                "§e➜ §fCentre des contrats"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMes contrats §6✦")
                        .lore(
                                "§8• §7Contrats client : §e" + myContracts,
                                "§8• §7Vos commandes passées",
                                "§8• §7Suivi et validation",
                                "",
                                "§e➜ §fOuvrir"
                        )
                        .action("contract_my_list")
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(
                        business != null
                                ? Material.LECTERN
                                : Material.GRAY_DYE
                )
                        .name("§6✦ §fContrats entreprise §6✦")
                        .lore(
                                business != null
                                        ? "§8• §7Entreprise : §e" + shortText(business.getName(), 18)
                                        : "§8• §7Aucune entreprise active",
                                business != null
                                        ? "§8• §7Contrats : §e" + ContractManager.getByBusiness(business).size()
                                        : "§8• §7Rejoignez ou créez une entreprise",
                                "§8• §7Travail à faire",
                                "§8• §7Paiements à valider",
                                "",
                                business != null
                                        ? "§e➜ §fOuvrir"
                                        : "§c✖ §fIndisponible"
                        )
                        .action(
                                business != null
                                        ? "contract_business_list"
                                        : "coming_soon"
                        )
                        .target(
                                business != null
                                        ? business.getId()
                                        : ""
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.IRON_PICKAXE)
                        .name("§6✦ §fMes missions §6✦")
                        .lore(
                                "§8• §7Missions assignées : §e" + myMissions,
                                "§8• §7Travail à effectuer",
                                "§8• §7Progression et paiement",
                                "",
                                "§e➜ §fOuvrir"
                        )
                        .action("contract_my_missions")
                        .build()
        );

        if (p.hasPermission("moodbusiness.staff.litige")) {

            SafeGUI.set(
                    inv,
                    31,
                    new ItemBuilder(Material.ANVIL)
                            .name("§6✦ §fLitiges staff §6✦")
                            .lore(
                                    "§8• §7Contrats signalés",
                                    "§8• §7Rembourser ou payer",
                                    "§8• §7Décision administrative",
                                    "",
                                    "§c✖ §fAccès staff"
                            )
                            .action("contract_litige_list")
                            .build()
            );
        }

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
