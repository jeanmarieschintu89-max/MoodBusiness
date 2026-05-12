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
            "§8✦ §6Historique Administratif §8✦";

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
                        .name("§6✦ §fJournal du Registre §6✦")
                        .lore(
                                "§7Dernières actions économiques.",
                                "§7Entrées affichées: §e" + logs.size(),
                                "",
                                "§8• §7Service officiel de §aMood§6Craft§7."
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
                                    "§7Acteur: §e" + entry.getActorName(),
                                    entry.getTargetName() == null || entry.getTargetName().isBlank()
                                            ? "§7Cible: §8Aucune"
                                            : "§7Cible: §f" + entry.getTargetName(),
                                    entry.getBusinessName() == null || entry.getBusinessName().isBlank()
                                            ? "§7Entreprise: §8Aucune"
                                            : "§7Entreprise: §b" + entry.getBusinessName(),
                                    "§7Date: §f" + TimeUtil.formatDate(entry.getCreatedAt()),
                                    "",
                                    "§8• §7" + crop(entry.getMessage())
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
                                "§7Revenir à la gestion staff."
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

    private static String crop(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return "Aucun détail.";
        }

        if (text.length() <= 60) {
            return text;
        }

        return text.substring(0, 60) + "...";
    }
}