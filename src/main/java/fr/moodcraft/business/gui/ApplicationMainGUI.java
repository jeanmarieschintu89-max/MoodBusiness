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
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        int active =
                ApplicationManager.countActiveByApplicant(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                13,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fPostuler §6✦")
                        .lore(
                                "§7Envoyer une candidature",
                                "§7à une entreprise.",
                                "",
                                "§8• §7Stage",
                                "§8• §7Apprentissage",
                                "",
                                "§7Actives: §e" + active,
                                "",
                                "§eClique pour commencer"
                        )
                        .action("application_choose_business")
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMes candidatures §6✦")
                        .lore(
                                "§7Voir vos candidatures",
                                "§7envoyées.",
                                "",
                                "§8• §7En attente",
                                "§8• §7Entretien",
                                "§8• §7Acceptée / refusée",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("application_my_list")
                        .build()
        );

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        if (business != null
                && BusinessManager.canManageRoles(
                p,
                business
        )) {

            SafeGUI.set(
                    inv,
                    23,
                    new ItemBuilder(Material.CHEST)
                            .name("§6✦ §fCandidatures reçues §6✦")
                            .lore(
                                    "§7Voir les joueurs",
                                    "§7qui veulent rejoindre.",
                                    "",
                                    "§7Entreprise: §e" + shortText(business.getName(), 18),
                                    "",
                                    "§8• §7Accepter",
                                    "§8• §7Refuser",
                                    "§8• §7Entretien",
                                    "",
                                    "§a✔ Gestion autorisée"
                            )
                            .action("application_received_list")
                            .target(business.getId())
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
