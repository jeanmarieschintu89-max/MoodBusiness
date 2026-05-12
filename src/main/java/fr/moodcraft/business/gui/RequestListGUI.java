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
            "§8✦ §6Demandes §8✦";

    public static final String TITLE_MY =
            "§8✦ §6Mes demandes §8✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private RequestListGUI() {}

    public static void openPublic(
            Player p
    ) {

        open(
                p,
                TITLE_PUBLIC,
                RequestManager.getPublicOpen()
        );
    }

    public static void openMy(
            Player p
    ) {

        open(
                p,
                TITLE_MY,
                RequestManager.getByPlayer(
                        p.getUniqueId()
                )
        );
    }

    private static void open(
            Player p,
            String title,
            List<BusinessRequest> list
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        title
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fDemandes §6✦")
                        .lore(
                                "§7Total: §e" + list.size(),
                                "§7Service: §aMood§6Craft",
                                "",
                                "§eSélectionnez un dossier"
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
                    new ItemBuilder(Material.PAPER)
                            .name(shortName(request.getTitle()))
                            .lore(
                                    "§7Auteur: §e" + shortText(request.getCreatorName(), 14),
                                    "§7Type: " + request.getCategory().getDisplayName(),
                                    "§7Budget: §e" + VaultHook.format(request.getBudget()),
                                    "§7Délai: §b" + request.getDueDays() + "j",
                                    "§7Statut: " + request.getStatus().getDisplayName(),
                                    "§7Fin: §f" + shortDate(request.getExpiresAt()),
                                    "",
                                    "§eClique pour ouvrir"
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
                        .name("§cRetour")
                        .lore(
                                "§7Menu demandes"
                        )
                        .action("open_requests")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortName(
            String text
    ) {

        return "§6✦ §f" + shortText(text, 22);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Sans nom";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(0, Math.max(1, max - 3)) + "...";
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