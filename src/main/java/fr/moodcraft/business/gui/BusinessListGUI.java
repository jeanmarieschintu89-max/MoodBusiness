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

    public static final String TITLE_ACTIVE =
            "§8✦ §6Entreprises Actives §8✦";

    public static final String TITLE_RECENT =
            "§8✦ §6Entreprises Récentes §8✦";

    public static final String TITLE_SUSPENDED =
            "§8✦ §6Entreprises Suspendues §8✦";

    private static final int PAGE_SIZE = 28;

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private BusinessListGUI() {}

    public static void openActive(
            Player p,
            int page
    ) {

        open(
                p,
                TITLE_ACTIVE,
                BusinessManager.getByStatus(
                        BusinessStatus.ACTIVE
                ),
                page,
                "ACTIVE"
        );
    }

    public static void openRecent(
            Player p,
            int page
    ) {

        open(
                p,
                TITLE_RECENT,
                BusinessManager.getRecent(),
                page,
                "RECENT"
        );
    }

    public static void openSuspended(
            Player p,
            int page
    ) {

        open(
                p,
                TITLE_SUSPENDED,
                BusinessManager.getByStatus(
                        BusinessStatus.SUSPENDUE
                ),
                page,
                "SUSPENDUE"
        );
    }

    private static void open(
            Player p,
            String title,
            List<Business> list,
            int page,
            String type
    ) {

        if (page < 1) {
            page = 1;
        }

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        title
                );

        SafeGUI.fill(inv);

        int maxPage =
                Math.max(
                        1,
                        (int) Math.ceil(
                                list.size() / (double) PAGE_SIZE
                        )
                );

        if (page > maxPage) {
            page = maxPage;
        }

        int start =
                (page - 1) * PAGE_SIZE;

        int end =
                Math.min(
                        start + PAGE_SIZE,
                        list.size()
                );

        int slotIndex = 0;

        for (int i = start; i < end; i++) {

            Business business =
                    list.get(i);

            Material material =
                    switch (business.getStatus()) {

                        case ACTIVE -> Material.LIME_BANNER;
                        case SUSPENDUE -> Material.RED_BANNER;
                        case ARCHIVEE -> Material.GRAY_BANNER;
                    };

            SafeGUI.set(
                    inv,
                    SLOTS[slotIndex],
                    new ItemBuilder(material)
                            .name("§6✦ §f" + business.getName() + " §6✦")
                            .lore(
                                    "§7Dirigeant: §e"
                                            + business.getOwnerName(),
                                    "§7Statut: "
                                            + business.getStatus()
                                            .getDisplayName(),
                                    "§7Solde entreprise: §e"
                                            + VaultHook.format(
                                            business.getBalance()
                                    ),
                                    "§7Frais création: §e"
                                            + VaultHook.format(
                                            business.getCreationFee()
                                    ),
                                    "§7Créée le: §f"
                                            + TimeUtil.formatDate(
                                            business.getCreatedAt()
                                    ),
                                    "",
                                    "§8• §7ID: §8"
                                            + business.getId()
                            )
                            .action("business_info")
                            .target(business.getId())
                            .build()
            );

            slotIndex++;
        }

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fPage " + page + " / " + maxPage + " §6✦")
                        .lore(
                                "§7Entrées: §e" + list.size()
                        )
                        .build()
        );

        if (page > 1) {

            SafeGUI.set(
                    inv,
                    45,
                    new ItemBuilder(Material.ARROW)
                            .name("§ePage précédente")
                            .lore(
                                    "§7Revenir à la page "
                                            + (page - 1)
                            )
                            .action("list_prev")
                            .target(type + ":" + (page - 1))
                            .build()
            );
        }

        if (page < maxPage) {

            SafeGUI.set(
                    inv,
                    53,
                    new ItemBuilder(Material.ARROW)
                            .name("§ePage suivante")
                            .lore(
                                    "§7Aller à la page "
                                            + (page + 1)
                            )
                            .action("list_next")
                            .target(type + ":" + (page + 1))
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir au registre principal."
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}