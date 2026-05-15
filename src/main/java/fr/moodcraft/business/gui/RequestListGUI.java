package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.BusinessRequest;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class RequestListGUI {

    public static final String TITLE_PUBLIC =
            GuiTitle.of("Demandes publiques");

    public static final String TITLE_MY =
            GuiTitle.of("Mes demandes");

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private RequestListGUI() {}

    public static void openPublic(Player p) {
        open(p, TITLE_PUBLIC, RequestManager.getPublicOpen(), false);
    }

    public static void openMy(Player p) {
        open(p, TITLE_MY, RequestManager.getByPlayer(p.getUniqueId()), true);
    }

    private static void open(Player p, String title, List<BusinessRequest> list, boolean ownList) {

        Inventory inv = Bukkit.createInventory(null, 54, title);
        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(ownList ? Material.WRITABLE_BOOK : Material.BOOK)
                        .name(ownList ? "§6✦ §fMes demandes §6✦" : "§6✦ §fDemandes publiques §6✦")
                        .lore(
                                "§8• §7Total : §e" + list.size(),
                                "§8• §7Service : §aMood§6Craft",
                                "",
                                ownList
                                        ? "§e➜ §fOuvrez une demande pour l'annuler"
                                        : "§e➜ §fSélectionnez un dossier"
                        )
                        .build()
        );

        int index = 0;

        for (BusinessRequest request : list) {

            if (index >= SLOTS.length) {
                break;
            }

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(request.getStatus().isOpen() ? Material.PAPER : Material.GRAY_DYE)
                            .name(shortName(request.getTitle()))
                            .lore(
                                    "§8• §7Auteur : §e" + shortText(request.getCreatorName(), 14),
                                    "§8• §7Type : " + request.getCategory().getDisplayName(),
                                    "§8• §7Budget : §e" + VaultHook.format(request.getBudget()),
                                    "§8• §7Délai : §b" + request.getDueDays() + "j",
                                    "§8• §7État : " + request.getStatus().getDisplayName(),
                                    "§8• §7Fin : §f" + shortDate(request.getExpiresAt()),
                                    "§8• §7ID : §8" + request.getId(),
                                    "",
                                    request.getStatus().isOpen()
                                            ? "§e➜ §fOuvrir"
                                            : "§8• §7Dossier fermé"
                            )
                            .action("request_detail")
                            .target(request.getId())
                            .build()
            );

            index++;
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§6✦ §fRetour §6✦")
                        .lore("§8• §7Menu demandes")
                        .action("open_requests")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortName(String text) {
        return "§6✦ §f" + shortText(text, 22) + " §6✦";
    }

    private static String shortText(String text, int max) {

        if (text == null || text.isBlank()) {
            return "Sans nom";
        }

        String clean = text.replaceAll("§.", "").trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private static String shortDate(long time) {

        String date = TimeUtil.formatDate(time);

        if (date == null || date.equalsIgnoreCase("Jamais")) {
            return "Aucune";
        }

        if (date.length() <= 10) {
            return date;
        }

        return date.substring(0, 10);
    }
}
