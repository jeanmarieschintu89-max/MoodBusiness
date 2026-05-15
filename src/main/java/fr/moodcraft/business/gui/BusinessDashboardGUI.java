package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.ApplicationManager;
import fr.moodcraft.business.manager.ContractManager;
import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessDashboardGUI {

    public static final String TITLE = GuiTitle.of("Mon entreprise");

    private BusinessDashboardGUI() {}

    public static void open(Player p, Business business) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        BusinessRole role = business.getRole(p.getUniqueId());
        int employees = Math.max(0, business.getMembers().size() - 1);
        int applications = ApplicationManager.getPendingByBusiness(business.getId()).size();
        int contracts = ContractManager.getByBusiness(business).size();
        int requests = RequestManager.getPublicOpen().size();

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.LECTERN)
                        .name("§6✦ §f" + shortText(business.getName(), 22) + " §6✦")
                        .lore(
                                "§8• §7Dirigeant : §e" + shortText(business.getOwnerName(), 18),
                                "§8• §7Ton rôle : " + (role != null ? role.getDisplayName() : "§7Membre"),
                                "§8• §7État : " + business.getStatus().getDisplayName(),
                                "§8• §7Banque : §e" + VaultHook.format(business.getBalance()),
                                "",
                                "§e➜ §fTableau de bord simplifié"
                        )
                        .build()
        );

        SafeGUI.set(inv, 19, new ItemBuilder(Material.PLAYER_HEAD)
                .name("§6✦ §fÉquipe §6✦")
                .lore(
                        "§8• §7Employés : §e" + employees,
                        "§8• §7Candidatures : §e" + applications,
                        "§8• §7Rôles et recrutement",
                        "§8• §7Gestion des membres",
                        "",
                        "§e➜ §fOuvrir l'équipe"
                )
                .action("dashboard_employees")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 21, new ItemBuilder(Material.GOLD_INGOT)
                .name("§6✦ §fArgent §6✦")
                .lore(
                        "§8• §7Solde : §e" + VaultHook.format(business.getBalance()),
                        "§8• §7Dépôts et retraits",
                        "§8• §7Primes",
                        "§8• §7Salaires mensuels",
                        "",
                        "§e➜ §fOuvrir la banque"
                )
                .action("dashboard_bank")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 23, new ItemBuilder(Material.WRITABLE_BOOK)
                .name("§6✦ §fContrats §6✦")
                .lore(
                        "§8• §7Contrats : §e" + contracts,
                        "§8• §7Missions acceptées",
                        "§8• §7Argent bloqué",
                        "§8• §7Litiges",
                        "",
                        "§e➜ §fOuvrir les contrats"
                )
                .action("dashboard_contracts")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 25, new ItemBuilder(Material.PAPER)
                .name("§6✦ §fMissions publiques §6✦")
                .lore(
                        "§8• §7Demandes ouvertes : §e" + requests,
                        "§8• §7Besoins des joueurs",
                        "§8• §7Prise en charge directe",
                        "§8• §7Contrat automatique",
                        "",
                        "§e➜ §fChercher une mission"
                )
                .action("dashboard_requests")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 40, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Bureau des Entreprises")
                .action("back_business_main")
                .build());

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
