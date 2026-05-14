package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.FinanceTransaction;

import fr.moodcraft.business.storage.FinanceStorage;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class BusinessFinanceHistoryGUI {

    public static final String TITLE =
            "§6✦ §8Logs financiers §6✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private BusinessFinanceHistoryGUI() {}

    public static void open(
            Player p,
            Business business
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        45,
                        TITLE
                );

        SafeGUI.fill(inv);

        List<FinanceTransaction> transactions =
                FinanceStorage.getByBusiness(
                        business.getId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fLogs financiers §6✦")
                        .lore(
                                "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                                "§8• §7Lignes : §e" + transactions.size(),
                                "§8• §7Dépôts, retraits, primes",
                                "§8• §7Paie, contrats et taxes",
                                "",
                                "§e➜ §fDerniers mouvements"
                        )
                        .build()
        );

        int index = 0;

        for (FinanceTransaction transaction : transactions) {

            if (index >= SLOTS.length) {
                break;
            }

            Material icon =
                    switch (transaction.getType()) {

                        case DEPOT -> Material.EMERALD;
                        case RETRAIT -> Material.REDSTONE;
                        case PRIME -> Material.SUNFLOWER;
                        case PAIE_MENSUELLE -> Material.CLOCK;
                        case CONTRAT_VERSEMENT -> Material.GOLD_INGOT;
                        case TAXE -> Material.PAPER;
                        case AJUSTEMENT -> Material.COMPARATOR;
                    };

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(icon)
                            .name("§6✦ " + transaction.getType().getDisplayName() + " §6✦")
                            .lore(
                                    "§8• §7Montant : §e" + VaultHook.format(transaction.getAmount()),
                                    "§8• §7Acteur : §f" + shortText(transaction.getActorName(), 16),
                                    targetLine(transaction),
                                    "§8• §7Date : §f" + shortDate(transaction.getCreatedAt()),
                                    "",
                                    "§8• §7" + shortText(transaction.getNote(), 32)
                            )
                            .build()
            );

            index++;
        }

        if (transactions.isEmpty()) {

            SafeGUI.set(
                    inv,
                    22,
                    new ItemBuilder(Material.PAPER)
                            .name("§6✦ §fAucun mouvement §6✦")
                            .lore(
                                    "§8• §7Aucun dépôt ou retrait",
                                    "§8• §7Aucun paiement enregistré",
                                    "",
                                    "§e➜ §fLes logs apparaîtront ici"
                            )
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.ARROW)
                        .name("§6✦ §fRetour §6✦")
                        .lore(
                                "§8• §7Argent entreprise"
                        )
                        .action("open_bank")
                        .target(business.getId())
                        .build()
        );

        p.openInventory(inv);
    }

    private static String targetLine(
            FinanceTransaction transaction
    ) {

        if (transaction.getTargetName() == null
                || transaction.getTargetName().isBlank()) {

            return "§8• §7Cible : §8Aucune";
        }

        return "§8• §7Cible : §f" + shortText(transaction.getTargetName(), 16);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Aucun";
        }

        String clean = text.replaceAll("§.", "").trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private static String shortDate(
            long time
    ) {

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
