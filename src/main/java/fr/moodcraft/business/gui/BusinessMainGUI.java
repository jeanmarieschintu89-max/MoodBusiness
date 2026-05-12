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
            "§8✦ §6Entreprises §aMood§6Craft §8✦";

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
                                    "§7Enregistrer une nouvelle entreprise",
                                    "§7dans le registre économique.",
                                    "",
                                    "§7Frais actuels: §e"
                                            + VaultHook.format(nextPrice),
                                    "§8• §7Première création: §e15 000€",
                                    "§8• §7Puis +15 000€ par création",
                                    "",
                                    "§a✔ Commande: §e/entreprise creer <nom>"
                            )
                            .action("main_create")
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
                    new ItemBuilder(Material.BOOK)
                            .name("§6✦ §fMon entreprise §6✦")
                            .lore(
                                    "§7Nom: §e" + business.getName(),
                                    "§7Rôle: "
                                            + (role != null
                                            ? role.getDisplayName()
                                            : "§7Membre"),
                                    "§7Statut: "
                                            + business.getStatus().getDisplayName(),
                                    "",
                                    "§eClique pour consulter le dossier."
                            )
                            .action("owned_info")
                            .target(business.getId())
                            .build()
            );

            SafeGUI.set(
                    inv,
                    20,
                    new ItemBuilder(Material.PLAYER_HEAD)
                            .name("§6✦ §fEmployés §6✦")
                            .lore(
                                    "§7Voir les employés, rôles,",
                                    "§7stages et apprentissages.",
                                    "",
                                    role != null && role.canManageRoles()
                                            ? "§a✔ Gestion autorisée"
                                            : "§7Consultation limitée"
                            )
                            .action("open_employees")
                            .target(business.getId())
                            .build()
            );

            SafeGUI.set(
                    inv,
                    22,
                    new ItemBuilder(Material.GOLD_INGOT)
                            .name("§6✦ §fBanque entreprise §6✦")
                            .lore(
                                    "§7Solde: §e"
                                            + VaultHook.format(
                                            business.getBalance()
                                    ),
                                    "",
                                    "§8• §7Contrats, salaires mensuels",
                                    "§8• §7et primes arriveront ensuite."
                            )
                            .action("coming_soon")
                            .build()
            );

            SafeGUI.set(
                    inv,
                    24,
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name("§6✦ §fContrats §6✦")
                            .lore(
                                    "§7Contrats sécurisés, délais,",
                                    "§7fonds bloqués et litiges.",
                                    "",
                                    "§8• §7Module prévu après les rôles."
                            )
                            .action("coming_soon")
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                29,
                new ItemBuilder(Material.COMPASS)
                        .name("§6✦ §fRegistre public §6✦")
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
                31,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fDemandes économiques §6✦")
                        .lore(
                                "§7Créer ou consulter des demandes",
                                "§7publiques pour les entreprises.",
                                "",
                                "§8• §7Bientôt disponible."
                        )
                        .action("coming_soon")
                        .build()
        );

        SafeGUI.set(
                inv,
                33,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fCandidatures §6✦")
                        .lore(
                                "§7Stage, apprentissage ou emploi",
                                "§7dans une entreprise.",
                                "",
                                "§a✔ Ouvrir le registre"
                        )
                        .action("open_applications")
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
                                    "§7suspensions et litiges.",
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