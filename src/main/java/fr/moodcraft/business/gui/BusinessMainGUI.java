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
            "§8✦ §6Bureau des Entreprises §8✦";

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
                                    "§7Lance une entreprise officielle",
                                    "§7sur §aMood§6Craft§7.",
                                    "",
                                    "§7Frais actuels: §e"
                                            + VaultHook.format(nextPrice),
                                    "§8• §7Première création: §e15 000€",
                                    "§8• §7Puis +15 000€ par création",
                                    "",
                                    "§a✔ Clique pour écrire le nom dans le chat"
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
                                    "§7Entreprise: §e" + business.getName(),
                                    "§7Votre rôle: "
                                            + (role != null
                                            ? role.getDisplayName()
                                            : "§7Membre"),
                                    "§7Statut: "
                                            + business.getStatus().getDisplayName(),
                                    "",
                                    "§8• §7Employés et rôles",
                                    "§8• §7Banque entreprise",
                                    "§8• §7Contrats",
                                    "§8• §7Candidatures",
                                    "§8• §7Demandes et offres",
                                    "",
                                    "§a✔ Ouvrir la gestion"
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
                        .name("§6✦ §fEntreprises publiques §6✦")
                        .lore(
                                "§7Consulter les entreprises actives",
                                "§7du serveur §aMood§6Craft§7.",
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
                                "§7Chercher un stage,",
                                "§7un apprentissage ou un emploi.",
                                "",
                                "§a✔ Ouvrir le registre"
                        )
                        .action("open_applications")
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fDemandes économiques §6✦")
                        .lore(
                                "§7Publier une demande ou",
                                "§7consulter les besoins publics.",
                                "",
                                "§a✔ Ouvrir les demandes"
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
                                    "§7Surveiller les entreprises,",
                                    "§7suspensions, litiges et logs.",
                                    "",
                                    "§cAccès administratif"
                            )
                            .action("open_staff")
                            .build()
            );
        }

        p.openInventory(inv);
    }
}