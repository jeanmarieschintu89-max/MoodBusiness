package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractManager;
import fr.moodcraft.business.manager.PayrollManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.storage.FinanceStorage;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessDashboardGUI {

    public static final String TITLE =
            "§8✦ §6Gestion Entreprise §8✦";

    private BusinessDashboardGUI() {}

    public static void open(
            Player p,
            Business business
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        BusinessRole role =
                business.getRole(
                        p.getUniqueId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.LECTERN)
                        .name("§6✦ §f" + business.getName() + " §6✦")
                        .lore(
                                "§7Service officiel de §aMood§6Craft§7.",
                                "",
                                "§7Dirigeant: §e" + business.getOwnerName(),
                                "§7Votre rôle: "
                                        + (role != null
                                        ? role.getDisplayName()
                                        : "§7Membre"),
                                "§7Statut: " + business.getStatus().getDisplayName(),
                                "§7Créée le: §f" + TimeUtil.formatDate(business.getCreatedAt()),
                                "",
                                "§7Solde entreprise: §e" + VaultHook.format(business.getBalance()),
                                "§7Paie mensuelle estimée: §e"
                                        + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(
                                                business
                                        )
                                ),
                                "§7Transactions: §e"
                                        + FinanceStorage.getByBusiness(
                                        business.getId()
                                ).size(),
                                "",
                                "§8• §7Bureau des Entreprises"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                19,
                new ItemBuilder(Material.PLAYER_HEAD)
                        .name("§6✦ §fEmployés et rôles §6✦")
                        .lore(
                                "§7Voir les membres de l'entreprise.",
                                "§7Gérer les rôles selon votre rang.",
                                "",
                                "§8• §7Dirigeant",
                                "§8• §7Gérant",
                                "§8• §7Trésorier",
                                "§8• §7Employé",
                                "§8• §7Apprenti / Stagiaire",
                                "",
                                role != null && role.canManageRoles()
                                        ? "§a✔ Gestion autorisée"
                                        : "§7Consultation limitée"
                        )
                        .action("dashboard_employees")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§6✦ §fBanque entreprise §6✦")
                        .lore(
                                "§7Gérer les fonds professionnels.",
                                "",
                                "§7Solde: §e" + VaultHook.format(business.getBalance()),
                                "",
                                "§8• §7Dépôts",
                                "§8• §7Retraits autorisés",
                                "§8• §7Primes",
                                "§8• §7Paie mensuelle",
                                "",
                                role != null && role.canManageBank()
                                        ? "§a✔ Accès bancaire"
                                        : "§7Lecture / accès limité"
                        )
                        .action("dashboard_bank")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fContrats officiels §6✦")
                        .lore(
                                "§7Suivre les contrats sécurisés.",
                                "",
                                "§7Contrats liés: §e"
                                        + ContractManager.getByBusiness(
                                        business
                                ).size(),
                                "",
                                "§8• §7Fonds bloqués",
                                "§8• §7Validation client",
                                "§8• §7Taxe économique 20%",
                                "§8• §7Litiges",
                                "",
                                role != null && role.canManageContracts()
                                        ? "§a✔ Gestion autorisée"
                                        : "§7Consultation"
                        )
                        .action("dashboard_contracts")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                25,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fCandidatures §6✦")
                        .lore(
                                "§7Gérer les demandes de stage",
                                "§7et d'apprentissage.",
                                "",
                                "§8• §7Stagiaires",
                                "§8• §7Apprentis",
                                "§8• §7Entretiens",
                                "",
                                role != null && role.canManageRoles()
                                        ? "§a✔ Examiner les dossiers"
                                        : "§7Non autorisé"
                        )
                        .action("dashboard_applications")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fDemandes et offres §6✦")
                        .lore(
                                "§7Consulter les demandes publiques",
                                "§7et proposer des offres.",
                                "",
                                "§8• §7Construction",
                                "§8• §7Commerce",
                                "§8• §7Services",
                                "§8• §7Livraisons",
                                "",
                                role != null && role.canManageContracts()
                                        ? "§a✔ Répondre aux demandes"
                                        : "§7Consultation"
                        )
                        .action("dashboard_requests")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir au Bureau des Entreprises."
                        )
                        .action("back_business_main")
                        .build()
        );

        p.openInventory(inv);
    }
}