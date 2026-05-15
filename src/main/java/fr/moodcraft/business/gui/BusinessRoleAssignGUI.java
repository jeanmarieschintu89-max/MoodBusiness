package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class BusinessRoleAssignGUI {

    public static final String TITLE = GuiTitle.of("Attribuer un Rôle");

    private BusinessRoleAssignGUI() {}

    public static void open(Player p, Business business, UUID targetUuid) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        String targetName = business.getMemberName(targetUuid);
        BusinessRole current = business.getRole(targetUuid);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.NAME_TAG)
                .name("§6✦ §fRôle de " + shortText(targetName, 14) + " §6✦")
                .lore(
                        "§8• §7Actuel : " + (current != null ? current.getDisplayName() : "§7Aucun"),
                        "§8• §7Dirigeant non attribuable ici",
                        "",
                        "§e➜ §fChoisis un nouveau rôle"
                )
                .build());

        addRole(inv, p, business, targetUuid, BusinessRole.GERANT, 11, Material.GOLDEN_HELMET, "Gestion avancée");
        addRole(inv, p, business, targetUuid, BusinessRole.RESPONSABLE_CONTRATS, 12, Material.WRITABLE_BOOK, "Contrats et offres");
        addRole(inv, p, business, targetUuid, BusinessRole.TRESORIER, 13, Material.GOLD_INGOT, "Banque entreprise");
        addRole(inv, p, business, targetUuid, BusinessRole.EMPLOYE, 14, Material.IRON_PICKAXE, "Membre officiel");
        addRole(inv, p, business, targetUuid, BusinessRole.APPRENTI, 15, Material.COPPER_INGOT, "Formation active");
        addRole(inv, p, business, targetUuid, BusinessRole.STAGIAIRE, 16, Material.PAPER, "Découverte");

        SafeGUI.set(inv, 40, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Liste des employés")
                .action("open_employees")
                .target(business.getId())
                .build());

        p.openInventory(inv);
    }

    private static void addRole(Inventory inv, Player p, Business business, UUID targetUuid, BusinessRole role, int slot, Material material, String description) {

        boolean allowed = BusinessManager.canAssignRole(p, business, targetUuid, role);

        SafeGUI.set(inv, slot, new ItemBuilder(allowed ? material : Material.GRAY_DYE)
                .name("§6✦ " + role.getDisplayName() + " §6✦")
                .lore(
                        "§8• §7" + description,
                        "§8• §7Pouvoir : §e" + role.getPower(),
                        "",
                        allowed ? "§e➜ §fAttribuer" : "§c✖ §fNon autorisé"
                )
                .action(allowed ? "assign_role" : "coming_soon")
                .target(business.getId() + ":" + targetUuid + ":" + role.name())
                .build());
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
