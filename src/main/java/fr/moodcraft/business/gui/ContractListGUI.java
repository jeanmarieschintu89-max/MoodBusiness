package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class ContractListGUI {

    public static final String TITLE_MY =
            "§6✦ §8Mes Contrats §6✦";

    public static final String TITLE_BUSINESS =
            "§6✦ §8Contrats Entreprise §6✦";

    public static final String TITLE_LITIGE =
            "§6✦ §8Litiges §6✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private ContractListGUI() {}

    public static void openMy(
            Player p
    ) {

        open(
                p,
                TITLE_MY,
                ContractManager.getByClient(p)
        );
    }

    public static void openBusiness(
            Player p,
            String businessId
    ) {

        Business business =
                BusinessManager.getById(businessId);

        open(
                p,
                TITLE_BUSINESS,
                ContractManager.getByBusiness(business)
        );
    }

    public static void openLitiges(
            Player p
    ) {

        open(
                p,
                TITLE_LITIGE,
                ContractManager.getLitiges()
        );
    }

    private static void open(
            Player p,
            String title,
            List<Contract> list
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
                        .name("§6✦ §fContrats §6✦")
                        .lore(
                                "§7Dossiers affichés: §e" + list.size(),
                                "",
                                "§8• §7Argent bloqué",
                                "§8• §7Validation",
                                "§8• §7Litige possible"
                        )
                        .build()
        );

        int index = 0;

        for (Contract contract : list) {

            if (index >= SLOTS.length) {
                break;
            }

            Material icon =
                    switch (contract.getStatus()) {

                        case EN_COURS -> Material.LIME_DYE;
                        case EN_RETARD -> Material.RED_DYE;
                        case TERMINE -> Material.YELLOW_DYE;
                        case VALIDE -> Material.GOLD_INGOT;
                        case LITIGE -> Material.ANVIL;
                        case ANNULE -> Material.GRAY_DYE;
                    };

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(icon)
                            .name("§6✦ §f" + shortText(contract.getTitle(), 22) + " §6✦")
                            .lore(
                                    "§7Client: §e" + shortText(contract.getClientName(), 14),
                                    "§7Entreprise: §b" + shortText(contract.getBusinessName(), 14),
                                    "§7État: " + contract.getStatus().getDisplayName(),
                                    "",
                                    "§7Bloqué: §e" + VaultHook.format(contract.getEscrowAmount()),
                                    "§7Brut: §e" + VaultHook.format(contract.getGrossAmount()),
                                    "§7Net: §a" + VaultHook.format(contract.getNetAmount()),
                                    "§7Délai: §f" + shortDate(contract.getDueAt()),
                                    "",
                                    "§eClique pour ouvrir"
                            )
                            .action("contract_detail")
                            .target(contract.getId())
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
                                "§7Menu contrats"
                        )
                        .action("open_contracts")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
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
