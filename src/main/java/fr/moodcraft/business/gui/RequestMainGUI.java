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
            "§6✦ §8Demandes §6✦";

    private RequestMainGUI() {}

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
                RequestManager.countActiveByPlayer(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fDemandes économiques §6✦")
                        .lore(
                                "§8• §7Publier un besoin",
                                "§8• §7Recevoir des offres",
                                "§8• §7Choisir une entreprise",
                                "",
                                "§e➜ §fCentre des demandes"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fCréer une demande §6✦")
                        .lore(
                                "§8• §7Titre",
                                "§8• §7Description",
                                "§8• §7Budget",
                                "§8• §7Délai",
                                "",
                                "§e➜ §fCommencer"
                        )
                        .action("request_create")
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.CHEST)
                        .name("§6✦ §fMes demandes §6✦")
                        .lore(
                                "§8• §7Demandes actives : §e" + active,
                                "§8• §7Voir les offres reçues",
                                "§8• §7Annuler une demande",
                                "",
                                "§e➜ §fGérer mes demandes"
                        )
                        .action("request_my_list")
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.COMPASS)
                        .name("§6✦ §fDemandes publiques §6✦")
                        .lore(
                                "§8• §7Besoins des joueurs",
                                "§8• §7Offres d'entreprises",
                                "§8• §7Missions ouvertes",
                                "",
                                "§e➜ §fVoir les demandes"
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
                            .name("§6✦ §fRépondre avec mon entreprise §6✦")
                            .lore(
                                    "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                                    "§8• §7Proposer un prix",
                                    "§8• §7Créer un contrat",
                                    "",
                                    "§a✔ §fAccès autorisé"
                            )
                            .action("request_public_list")
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
