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
            "§8✦ §6Mes Contrats §8✦";

    public static final String TITLE_BUSINESS =
            "§8✦ §6Contrats Entreprise §8✦";

    public static final String TITLE_LITIGE =
            "§8✦ §6Litiges Économiques §8✦";

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
                                "§8• §7Service officiel de §aMood§6Craft§7."
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
                            .name("§6✦ §f" + contract.getTitle() + " §6✦")
                            .lore(
                                    "§7Client: §e" + contract.getClientName(),
                                    "§7Entreprise: §b" + contract.getBusinessName(),
                                    "§7Statut: " + contract.getStatus().getDisplayName(),
                                    "§7Montant bloqué: §e" + VaultHook.format(contract.getEscrowAmount()),
                                    "§7Brut: §e" + VaultHook.format(contract.getGrossAmount()),
                                    "§7Net entreprise: §a" + VaultHook.format(contract.getNetAmount()),
                                    "§7Échéance: §f" + TimeUtil.formatDate(contract.getDueAt()),
                                    "",
                                    "§eClique pour consulter"
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
                                "§7Revenir au menu contrats."
                        )
                        .action("open_contracts")
                        .build()
        );

        p.openInventory(inv);
    }
}