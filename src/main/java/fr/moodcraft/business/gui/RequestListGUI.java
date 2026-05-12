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
            "§8✦ §6Demandes Publiques §8✦";

    public static final String TITLE_MY =
            "§8✦ §6Mes Demandes §8✦";

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
                                "§7Dossiers affichés: §e" + list.size(),
                                "",
                                "§8• §7Service officiel de §aMood§6Craft§7."
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
                            .name("§6✦ §f" + request.getTitle() + " §6✦")
                            .lore(
                                    "§7Demandeur: §e" + request.getCreatorName(),
                                    "§7Catégorie: " + request.getCategory().getDisplayName(),
                                    "§7Budget: §e" + VaultHook.format(request.getBudget()),
                                    "§7Délai souhaité: §b" + request.getDueDays() + " jours",
                                    "§7Statut: " + request.getStatus().getDisplayName(),
                                    "§7Expire le: §f" + TimeUtil.formatDate(request.getExpiresAt()),
                                    "",
                                    "§eClique pour consulter"
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
                                "§7Revenir au menu demandes."
                        )
                        .action("open_requests")
                        .build()
        );

        p.openInventory(inv);
    }
}