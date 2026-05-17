package fr.moodcraft.business.gui;

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
        int contracts = ContractManager.getByBusiness(business).size();
        int missions = RequestManager.getPublicOpen().size();

        boolean canClose = business.isOwner(p.getUniqueId())
                || role == BusinessRole.GERANT
                || p.hasPermission("moodbusiness.staff.suspend");

        SafeGUI.set(inv, 4,
                new ItemBuilder(Material.LECTERN)
                        .name("§6✦ §f" + shortText(business.getName(), 22) + " §6✦")
                        .lore(
                                "§8• §7Patron : §e" + shortText(business.getOwnerName(), 18),
                                "§8• §7Ton rôle : " + (role != null ? role.getDisplayName() : "§7Membre"),
                                "§8• §7État : " + business.getStatus().getDisplayName(),
                                "§8• §7Argent : §e" + VaultHook.format(business.getBalance()),
                                "",
                                "§e➜ §fTableau de bord simple"
                        )
                        .build()
        );

        SafeGUI.set(inv, 20, new ItemBuilder(Material.PLAYER_HEAD)
                .name("§6✦ §fÉquipe §6✦")
                .lore(
                        "§8• §7Employés : §e" + employees,
                        "§8• §7Recruter ou retirer un membre",
                        "§8• §7Rôles simplifiés côté joueur",
                        "",
                        "§e➜ §fGérer l'équipe"
                )
                .action("dashboard_employees")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 22, new ItemBuilder(Material.WRITABLE_BOOK)
                .name("§6✦ §fMissions §6✦")
                .lore(
                        "§8• §7Missions en cours : §e" + contracts,
                        "§8• §7Missions disponibles : §e" + missions,
                        "§8• §7Prendre, terminer, suivre",
                        "",
                        "§e➜ §fOuvrir les missions"
                )
                .action("dashboard_requests")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 24, new ItemBuilder(Material.GOLD_INGOT)
                .name("§6✦ §fArgent §6✦")
                .lore(
                        "§8• §7Solde : §e" + VaultHook.format(business.getBalance()),
                        "§8• §7Déposer ou retirer",
                        "§8• §7Paiements de missions validées",
                        "",
                        "§e➜ §fOuvrir l'argent"
                )
                .action("dashboard_bank")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 31, new ItemBuilder(canClose ? Material.LAVA_BUCKET : Material.GRAY_DYE)
                .name("§c✦ §fFermer l’entreprise §c✦")
                .lore(
                        "§8• §7Archive cette entreprise",
                        "§8• §7Argent vide requis",
                        "§8• §7Aucune mission ouverte",
                        "",
                        canClose
                                ? "§c✖ §fOuvrir la confirmation"
                                : "§8• §7Réservé au patron ou manager"
                )
                .action("dashboard_dissolve")
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
