package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessAdminManageGUI {

    public static final String TITLE = GuiTitle.of("Admin Entreprise");

    private BusinessAdminManageGUI() {}

    public static void open(Player p, Business business) {

        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        SafeGUI.fill(inv);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.NETHER_STAR)
                .name("§6✦ §f" + shortText(business.getName(), 22) + " §6✦")
                .lore(
                        "§8• §7Gestion staff",
                        "§8• §7Dirigeant : §e" + shortText(business.getOwnerName(), 18),
                        "§8• §7État : " + business.getStatus().getDisplayName(),
                        "§8• §7Banque : §e" + VaultHook.format(business.getBalance()),
                        "",
                        "§8• §7Voir",
                        "§8• §7Suspendre",
                        "§8• §7Réactiver",
                        "§8• §7Fermer"
                )
                .build());

        SafeGUI.set(inv, 20, new ItemBuilder(Material.LIME_BANNER)
                .name("§6✦ §aRéactiver §6✦")
                .lore(
                        "§8• §7Rouvre cette entreprise",
                        "§8• §7si elle est suspendue",
                        "§8• §7Elle pourra reprendre son activité",
                        "",
                        "§a✔ §fRéactiver"
                )
                .action("admin_reactivate_business")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 22, new ItemBuilder(Material.RED_BANNER)
                .name("§6✦ §cSuspendre §6✦")
                .lore(
                        "§8• §7Bloque cette entreprise",
                        "§8• §7temporairement",
                        "§8• §7Gestion limitée",
                        "",
                        "§c✖ §fSuspendre"
                )
                .action("admin_suspend_business")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 24, new ItemBuilder(Material.LAVA_BUCKET)
                .name("§6✦ §cFermer / Archiver §6✦")
                .lore(
                        "§8• §7Archive cette entreprise",
                        "§8• §7Elle ne sera plus active",
                        "§8• §7Banque vide requise",
                        "§8• §7Aucun contrat ouvert",
                        "",
                        "§c✖ §fAction sensible"
                )
                .action("admin_dissolve_business")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 31, new ItemBuilder(Material.BOOK)
                .name("§6✦ §fVoir la fiche §6✦")
                .lore(
                        "§8• §7Affiche les infos",
                        "§8• §7dans le chat",
                        "",
                        "§e➜ §fVoir"
                )
                .action("admin_info_business")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 49, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Gestion staff")
                .action("open_staff")
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
