package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.ApplicationManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class ApplicationMainGUI {

    public static final String TITLE =
            "§6✦ §8Candidatures §6✦";

    private ApplicationMainGUI() {}

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

        int active =
                ApplicationManager.countActiveByApplicant(
                        p.getUniqueId()
                );

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fCandidatures §6✦")
                        .lore(
                                "§8• §7Trouver une entreprise",
                                "§8• §7Postuler en stage",
                                "§8• §7Postuler en apprentissage",
                                "§8• §7Gérer les reçues",
                                "",
                                "§e➜ §fCentre de l'emploi"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fPostuler §6✦")
                        .lore(
                                "§8• §7Choisir une entreprise",
                                "§8• §7Type : stage ou apprentissage",
                                "§8• §7Message dans le chat",
                                "§8• §7Actives : §e" + active,
                                "",
                                "§e➜ §fCommencer"
                        )
                        .action("application_choose_business")
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMes candidatures §6✦")
                        .lore(
                                "§8• §7Candidatures envoyées",
                                "§8• §7En attente",
                                "§8• §7Entretien",
                                "§8• §7Acceptée ou refusée",
                                "",
                                "§e➜ §fVoir mes candidatures"
                        )
                        .action("application_my_list")
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(
                        business != null
                                && BusinessManager.canManageRoles(p, business)
                                ? Material.CHEST
                                : Material.GRAY_DYE
                )
                        .name("§6✦ §fCandidatures reçues §6✦")
                        .lore(
                                business != null
                                        ? "§8• §7Entreprise : §e" + shortText(business.getName(), 18)
                                        : "§8• §7Aucune entreprise active",
                                "§8• §7Examiner",
                                "§8• §7Accepter",
                                "§8• §7Refuser",
                                "",
                                business != null
                                        && BusinessManager.canManageRoles(p, business)
                                        ? "§e➜ §fGérer les reçues"
                                        : "§c✖ §fAccès limité"
                        )
                        .action(
                                business != null
                                        && BusinessManager.canManageRoles(p, business)
                                        ? "application_received_list"
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
