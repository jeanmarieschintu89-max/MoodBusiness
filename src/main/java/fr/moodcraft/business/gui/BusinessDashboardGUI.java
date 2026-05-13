package fr.moodcraft.business.gui;

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

        boolean canManageRoles =
                role != null
                        && role.canManageRoles();

        boolean canManageBank =
                role != null
                        && role.canManageBank();

        boolean canManageContracts =
                role != null
                        && role.canManageContracts();

        boolean canCloseBusiness =
                business.isOwner(p.getUniqueId())
                        || role == BusinessRole.GERANT;

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.LECTERN)
                        .name("§6✦ §f" + shortText(business.getName(), 22) + " §6✦")
                        .lore(
                                "§7Fiche de ton entreprise.",
                                "",
                                "§7Dirigeant: §e" + shortText(business.getOwnerName(), 18),
                                "§7Ton rôle: "
                                        + (role != null
                                        ? role.getDisplayName()
                                        : "§7Membre"),
                                "§7État: " + business.getStatus().getDisplayName(),
                                "§7Créée: §f" + shortDate(business.getCreatedAt()),
                                "",
                                "§7Banque: §e" + VaultHook.format(business.getBalance()),
                                "§7Paie/mois: §e"
                                        + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(
                                                business
                                        )
                                ),
                                "§7Mouvements: §e"
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
                        .name("§6✦ §fEmployés §6✦")
                        .lore(
                                "§7Gère les membres",
                                "§7de l'entreprise.",
                                "",
                                "§8• §7Rôles",
                                "§8• §7Stagiaires",
                                "§8• §7Apprentis",
                                "",
                                canManageRoles
                                        ? "§a✔ Gestion autorisée"
                                        : "§7Lecture seule"
                        )
                        .action("dashboard_employees")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§6✦ §fBanque §6✦")
                        .lore(
                                "§7Gère l'argent",
                                "§7de l'entreprise.",
                                "",
                                "§7Solde: §e" + VaultHook.format(business.getBalance()),
                                "",
                                "§8• §7Dépôt",
                                "§8• §7Retrait",
                                "§8• §7Primes",
                                "§8• §7Paie mensuelle",
                                "",
                                canManageBank
                                        ? "§a✔ Accès autorisé"
                                        : "§7Accès limité"
                        )
                        .action("dashboard_bank")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fContrats §6✦")
                        .lore(
                                "§7Suis les contrats",
                                "§7de l'entreprise.",
                                "",
                                "§7Contrats: §e"
                                        + ContractManager.getByBusiness(
                                        business
                                ).size(),
                                "",
                                "§8• §7Argent bloqué",
                                "§8• §7Taxe 20%",
                                "§8• §7Litiges",
                                "",
                                canManageContracts
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
                                "§7Voir les demandes",
                                "§7pour rejoindre l'entreprise.",
                                "",
                                "§8• §7Stage",
                                "§8• §7Apprentissage",
                                "§8• §7Entretien",
                                "",
                                canManageRoles
                                        ? "§a✔ Examiner"
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
                        .name("§6✦ §fDemandes §6✦")
                        .lore(
                                "§7Voir les besoins",
                                "§7des joueurs.",
                                "",
                                "§8• §7Construction",
                                "§8• §7Livraison",
                                "§8• §7Service",
                                "§8• §7Commerce",
                                "",
                                canManageContracts
                                        ? "§a✔ Répondre"
                                        : "§7Consultation"
                        )
                        .action("dashboard_requests")
                        .target(business.getId())
                        .build()
        );

        if (canCloseBusiness) {

            SafeGUI.set(
                    inv,
                    45,
                    new ItemBuilder(Material.BARRIER)
                            .name("§c✦ Fermer l'entreprise")
                            .lore(
                                    "§7Archive l'entreprise.",
                                    "§7Elle ne sera plus active.",
                                    "",
                                    "§8• §7Banque vide requise",
                                    "§8• §7Aucun contrat ouvert",
                                    "§8• §7Historique gardé",
                                    "",
                                    "§cAction sensible"
                            )
                            .action("dashboard_dissolve")
                            .target(business.getId())
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.ARROW)
                        .name("§cRetour")
                        .lore(
                                "§7Bureau des Entreprises"
                        )
                        .action("back_business_main")
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

    private static String shortDate(
            long time
    ) {

        String date =
                TimeUtil.formatDate(time);

        if (date == null || date.equalsIgnoreCase("Jamais")) {
            return "Aucune";
        }

        if (date.length() <= 10) {
            return date;
        }

        return date.substring(0, 10);
    }
}