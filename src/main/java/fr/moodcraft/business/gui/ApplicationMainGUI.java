package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ApplicationManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class ApplicationMainGUI {

    public static final String TITLE =
            "§8✦ §6Candidatures §aMood§6Craft §8✦";

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
                        .name("§6✦ §fEnvoyer une candidature §6✦")
                        .lore(
                                "§7Postuler auprès d'une entreprise",
                                "§7pour un stage ou un apprentissage.",
                                "",
                                "§7Candidatures actives: §e" + active,
                                "",
                                "§a✔ Formulaire guidé"
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
                                "§7Voir vos demandes de stage",
                                "§7et d'apprentissage.",
                                "",
                                "§eClique pour consulter"
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
                                    "§7Examiner les candidatures",
                                    "§7reçues par votre entreprise.",
                                    "",
                                    "§7Entreprise: §e" + business.getName(),
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
                                "§7Revenir au registre principal."
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}