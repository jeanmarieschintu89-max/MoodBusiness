package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class RequestMainGUI {

    public static final String TITLE =
            "§8✦ §6Demandes §8✦";

    private RequestMainGUI() {}

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
                RequestManager.countActiveByPlayer(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                13,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fCréer une demande §6✦")
                        .lore(
                                "§7Publier un besoin",
                                "§7pour une entreprise.",
                                "",
                                "§7Demandes actives: §e" + active,
                                "",
                                "§8• §7Titre",
                                "§8• §7Description",
                                "§8• §7Budget",
                                "§8• §7Délai",
                                "",
                                "§eClique pour commencer"
                        )
                        .action("request_create")
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMes demandes §6✦")
                        .lore(
                                "§7Voir vos demandes",
                                "§7et les offres reçues.",
                                "",
                                "§8• §7Demandes ouvertes",
                                "§8• §7Offres proposées",
                                "§8• §7Contrats créés",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("request_my_list")
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.COMPASS)
                        .name("§6✦ §fDemandes publiques §6✦")
                        .lore(
                                "§7Voir les besoins",
                                "§7des autres joueurs.",
                                "",
                                "§8• §7Construction",
                                "§8• §7Livraison",
                                "§8• §7Commerce",
                                "§8• §7Service",
                                "",
                                "§eClique pour voir"
                        )
                        .action("request_public_list")
                        .build()
        );

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        if (business != null
                && BusinessManager.canManageContracts(
                p,
                business
        )) {

            SafeGUI.set(
                    inv,
                    31,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fRépondre aux demandes §6✦")
                            .lore(
                                    "§7Envoyer une offre",
                                    "§7avec votre entreprise.",
                                    "",
                                    "§7Entreprise: §e" + shortText(business.getName(), 18),
                                    "",
                                    "§8• §7Montant",
                                    "§8• §7Délai",
                                    "§8• §7Commentaire",
                                    "",
                                    "§a✔ Accès autorisé"
                            )
                            .action("request_public_list")
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