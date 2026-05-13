package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.AuditLogEntry;

import fr.moodcraft.business.storage.AuditLogStorage;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class AuditLogGUI {

    public static final String TITLE =
            "§6✦ §8Logs §6✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private AuditLogGUI() {}

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

        List<AuditLogEntry> logs =
                AuditLogStorage.getRecent(28);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fLogs du Bureau §6✦")
                        .lore(
                                "§7Dernières actions",
                                "§7du Bureau des Entreprises.",
                                "",
                                "§7Affichées: §e" + logs.size(),
                                "",
                                "§8• §7Entreprises",
                                "§8• §7Contrats",
                                "§8• §7Banque",
                                "§8• §7Staff"
                        )
                        .build()
        );

        int index = 0;

        for (AuditLogEntry entry : logs) {

            if (index >= SLOTS.length) {
                break;
            }

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(icon(entry))
                            .name("§6✦ " + entry.getType().getDisplayName() + " §6✦")
                            .lore(
                                    "§7Acteur: §e" + shortText(entry.getActorName(), 16),
                                    targetLine(entry),
                                    businessLine(entry),
                                    "§7Date: §f" + shortDate(entry.getCreatedAt()),
                                    "",
                                    "§8• §7" + shortText(entry.getMessage(), 34)
                            )
                            .build()
            );

            index++;
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Gestion staff"
                        )
                        .action("open_staff")
                        .build()
        );

        p.openInventory(inv);
    }

    private static Material icon(
            AuditLogEntry entry
    ) {

        return switch (entry.getType()) {

            case BUSINESS_CREATED -> Material.EMERALD;
            case BUSINESS_SUSPENDED -> Material.RED_BANNER;
            case BUSINESS_REACTIVATED -> Material.LIME_BANNER;
            case MEMBER_ADDED -> Material.PLAYER_HEAD;
            case MEMBER_REMOVED -> Material.SKELETON_SKULL;
            case ROLE_CHANGED -> Material.NAME_TAG;
            case APPLICATION_CREATED, APPLICATION_UPDATED -> Material.PAPER;
            case REQUEST_CREATED -> Material.WRITABLE_BOOK;
            case OFFER_CREATED, OFFER_ACCEPTED -> Material.MAP;
            case CONTRACT_CREATED, CONTRACT_COMPLETED, CONTRACT_VALIDATED -> Material.BOOK;
            case CONTRACT_LITIGE -> Material.ANVIL;
            case BANK_DEPOSIT -> Material.EMERALD;
            case BANK_WITHDRAW -> Material.REDSTONE;
            case BONUS_PAID -> Material.SUNFLOWER;
            case PAYROLL_PAID -> Material.CLOCK;
            case PAYROLL_BLOCKED -> Material.BARRIER;
            case STAFF_ACTION -> Material.NETHER_STAR;
            case SYSTEM -> Material.COMPARATOR;
        };
    }

    private static String targetLine(
            AuditLogEntry entry
    ) {

        if (entry.getTargetName() == null
                || entry.getTargetName().isBlank()) {

            return "§7Cible: §8Aucune";
        }

        return "§7Cible: §f" + shortText(entry.getTargetName(), 16);
    }

    private static String businessLine(
            AuditLogEntry entry
    ) {

        if (entry.getBusinessName() == null
                || entry.getBusinessName().isBlank()) {

            return "§7Entreprise: §8Aucune";
        }

        return "§7Entreprise: §b" + shortText(entry.getBusinessName(), 16);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Aucun";
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
