package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessStatus;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class BusinessListGUI {

    public static final String TITLE_ACTIVE = GuiTitle.of("Entreprises Actives");
    public static final String TITLE_RECENT = GuiTitle.of("Entreprises Récentes");
    public static final String TITLE_SUSPENDED = GuiTitle.of("Entreprises Suspendues");

    private static final int PAGE_SIZE = 28;

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private BusinessListGUI() {}

    public static void openActive(Player p, int page) {
        open(p, TITLE_ACTIVE, BusinessManager.getByStatus(BusinessStatus.ACTIVE), page, "ACTIVE");
    }

    public static void openRecent(Player p, int page) {
        open(p, TITLE_RECENT, BusinessManager.getRecent(), page, "RECENT");
    }

    public static void openSuspended(Player p, int page) {
        open(p, TITLE_SUSPENDED, BusinessManager.getByStatus(BusinessStatus.SUSPENDUE), page, "SUSPENDUE");
    }

    private static void open(Player p, String title, List<Business> list, int page, String type) {

        if (page < 1) {
            page = 1;
        }

        Inventory inv = Bukkit.createInventory(null, 54, title);
        SafeGUI.fill(inv);

        int maxPage = Math.max(1, (int) Math.ceil(list.size() / (double) PAGE_SIZE));

        if (page > maxPage) {
            page = maxPage;
        }

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, list.size());
        boolean staff = p.hasPermission("moodbusiness.staff");
        int slotIndex = 0;

        for (int i = start; i < end; i++) {
            Business business = list.get(i);

            Material material = switch (business.getStatus()) {
                case ACTIVE -> Material.LIME_BANNER;
                case SUSPENDUE -> Material.RED_BANNER;
                case ARCHIVEE -> Material.GRAY_BANNER;
            };

            SafeGUI.set(inv, SLOTS[slotIndex], new ItemBuilder(material)
                    .name("§6✦ §f" + shortText(business.getName(), 18) + " §6✦")
                    .lore(
                            "§8• §7Dirigeant : §e" + shortText(business.getOwnerName(), 16),
                            "§8• §7État : " + business.getStatus().getDisplayName(),
                            "§8• §7Banque : §e" + VaultHook.format(business.getBalance()),
                            "§8• §7Créée : §f" + shortDate(business.getCreatedAt()),
                            "",
                            staff ? "§c✖ §fAdministrer" : "§e➜ §fConsulter"
                    )
                    .action(staff ? "admin_manage_business" : "business_info")
                    .target(business.getId())
                    .build());

            slotIndex++;
        }

        SafeGUI.set(inv, 4, new ItemBuilder(Material.BOOK)
                .name("§6✦ §fPage " + page + "/" + maxPage + " §6✦")
                .lore("§8• §7Entreprises : §e" + list.size())
                .build());

        if (page > 1) {
            SafeGUI.set(inv, 45, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .name("§6✦ §fPage précédente §6✦")
                    .lore("§8• §7Page " + (page - 1))
                    .action("list_prev")
                    .target(type + ":" + (page - 1))
                    .build());
        }

        if (page < maxPage) {
            SafeGUI.set(inv, 53, new ItemBuilder(Material.SPECTRAL_ARROW)
                    .name("§6✦ §fPage suivante §6✦")
                    .lore("§8• §7Page " + (page + 1))
                    .action("list_next")
                    .target(type + ":" + (page + 1))
                    .build());
        }

        SafeGUI.set(inv, 49, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Bureau des Entreprises")
                .action("back_main")
                .build());

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private static String shortDate(long time) {
        String date = TimeUtil.formatDate(time);
        if (date == null || date.equalsIgnoreCase("Jamais")) return "Aucune";
        if (date.length() <= 10) return date;
        return date.substring(0, 10);
    }
}
