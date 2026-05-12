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
            "§8✦ §6Demandes §aMood§6Craft §8✦";

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
                                "§7Publier un besoin officiel",
                                "§7pour les entreprises.",
                                "",
                                "§7Demandes actives: §e" + active,
                                "",
                                "§a✔ Formulaire guidé"
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
                                "§7Consulter vos demandes",
                                "§7et les offres reçues.",
                                "",
                                "§eClique pour consulter"
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
                                "§7Voir les demandes ouvertes",
                                "§7aux entreprises du serveur.",
                                "",
                                "§bRegistre public"
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
                                    "§7Votre entreprise peut envoyer",
                                    "§7des offres aux demandes publiques.",
                                    "",
                                    "§7Entreprise: §e" + business.getName(),
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
                                "§7Revenir au registre principal."
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}