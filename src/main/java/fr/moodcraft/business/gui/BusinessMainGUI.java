package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessMainGUI {

    public static final String TITLE =
        "§6✦ §8Bureau des Entreprises §6✦";

    private BusinessMainGUI() {}

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

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        double nextPrice =
                BusinessManager.getCreationPrice(
                        p.getUniqueId()
                );

        if (business == null) {

            SafeGUI.set(
                    inv,
                    13,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fCréer une entreprise §6✦")
                            .lore(
                                    "§7Lance ton activité",
                                    "§7sur §aMood§6Craft§7.",
                                    "",
                                    "§7Frais d'enregistrement:",
                                    "§e" + VaultHook.format(nextPrice),
                                    "",
                                    "§8• §7Nom dans le chat",
                                    "§8• §7Création directe",
                                    "§8• §7+15 000€ ensuite",
                                    "",
                                    "§a✔ Clique pour commencer"
                            )
                            .action("business_creation_chat")
                            .build()
            );

        } else {

            BusinessRole role =
                    business.getRole(
                            p.getUniqueId()
                    );

            SafeGUI.set(
                    inv,
                    13,
                    new ItemBuilder(Material.LECTERN)
                            .name("§6✦ §fGestion d'entreprise §6✦")
                            .lore(
                                    "§7Entreprise: §e" + shortText(business.getName(), 18),
                                    "§7Rôle: "
                                            + (role != null
                                            ? role.getDisplayName()
                                            : "§7Membre"),
                                    "§7État: "
                                            + business.getStatus().getDisplayName(),
                                    "",
                                    "§8• §7Employés",
                                    "§8• §7Banque",
                                    "§8• §7Contrats",
                                    "§8• §7Candidatures",
                                    "§8• §7Demandes",
                                    "",
                                    "§a✔ Ouvrir"
                            )
                            .action("open_business_dashboard")
                            .target(business.getId())
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.COMPASS)
                        .name("§6✦ §fEntreprises §6✦")
                        .lore(
                                "§7Voir les entreprises",
                                "§7actives du serveur.",
                                "",
                                "§8• §7Trouver une entreprise",
                                "§8• §7Voir les infos publiques",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("open_public_active")
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fCandidatures §6✦")
                        .lore(
                                "§7Demande pour rejoindre",
                                "§7une entreprise.",
                                "",
                                "§8• §7Stage",
                                "§8• §7Apprentissage",
                                "§8• §7Emploi",
                                "",
                                "§eClique pour postuler"
                        )
                        .action("open_applications")
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fDemandes §6✦")
                        .lore(
                                "§7Publier un besoin",
                                "§7pour une entreprise.",
                                "",
                                "§8• §7Construction",
                                "§8• §7Livraison",
                                "§8• §7Service",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("open_requests")
                        .build()
        );

        if (p.hasPermission("moodbusiness.staff")) {

            SafeGUI.set(
                    inv,
                    49,
                    new ItemBuilder(Material.NETHER_STAR)
                            .name("§6✦ §fGestion staff §6✦")
                            .lore(
                                    "§7Gérer les entreprises",
                                    "§7et les litiges.",
                                    "",
                                    "§8• §7Suspensions",
                                    "§8• §7Logs",
                                    "§8• §7Litiges",
                                    "",
                                    "§cAccès staff"
                            )
                            .action("open_staff")
                            .build()
            );
        }

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