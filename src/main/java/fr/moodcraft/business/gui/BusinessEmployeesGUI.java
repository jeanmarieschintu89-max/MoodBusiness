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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BusinessEmployeesGUI {

    public static final String TITLE = GuiTitle.of("Équipe entreprise");

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private BusinessEmployeesGUI() {}

    public static void open(Player p, Business business) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        boolean canManage = BusinessManager.canManageRoles(p, business);
        int employees = Math.max(0, business.getMembers().size() - 1);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.PLAYER_HEAD)
                .name("§6✦ §fÉquipe §6✦")
                .lore(
                        "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                        "§8• §7Membres : §e" + business.getMembers().size(),
                        "§8• §7Employés : §e" + employees,
                        "§8• §7Gestion : " + (canManage ? "§aoui" : "§cnon"),
                        "",
                        canManage ? "§e➜ §fClique un membre pour gérer" : "§e➜ §fLecture seule"
                )
                .build());

        List<Map.Entry<UUID, BusinessRole>> members = new ArrayList<>(business.getMembers().entrySet());
        int index = 0;

        for (Map.Entry<UUID, BusinessRole> entry : members) {
            if (index >= SLOTS.length) break;

            UUID uuid = entry.getKey();
            BusinessRole role = entry.getValue();

            Material icon = switch (role) {
                case DIRIGEANT -> Material.NETHER_STAR;
                case GERANT -> Material.GOLDEN_HELMET;
                case RESPONSABLE_CONTRATS -> Material.WRITABLE_BOOK;
                case TRESORIER -> Material.GOLD_INGOT;
                case EMPLOYE -> Material.IRON_PICKAXE;
                case APPRENTI -> Material.COPPER_INGOT;
                case STAGIAIRE -> Material.PAPER;
            };

            List<String> lore = new ArrayList<>();
            lore.add("§8• §7Rôle : " + role.getDisplayName());

            if (business.isOwner(uuid)) {
                lore.add("§8• §7Dirigeant officiel");
            } else if (canManage) {
                lore.add("§e➜ §fGérer ce membre");
            } else {
                lore.add("§8• §7Lecture seule");
            }

            SafeGUI.set(inv, SLOTS[index], new ItemBuilder(icon)
                    .name("§6✦ §f" + shortText(business.getMemberName(uuid), 18) + " §6✦")
                    .lore(lore.toArray(new String[0]))
                    .action("employee_card")
                    .target(business.getId() + ":" + uuid)
                    .build());

            index++;
        }

        SafeGUI.set(inv, 36, new ItemBuilder(Material.EMERALD)
                .name("§6✦ §fRecruter §6✦")
                .lore(
                        "§8• §7Ajouter un joueur",
                        "§8• §7Pseudo dans le chat",
                        "§8• §7Rôle dans le chat",
                        "",
                        canManage ? "§e➜ §fCommencer le recrutement" : "§c✖ §fAccès limité"
                )
                .action(canManage ? "employee_recruit_chat" : "coming_soon")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 40, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Mon entreprise")
                .action("open_business_dashboard")
                .target(business.getId())
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
