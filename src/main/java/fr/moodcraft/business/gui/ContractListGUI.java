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

    public static final String TITLE_MY = GuiTitle.of("Mes Contrats");
    public static final String TITLE_BUSINESS = GuiTitle.of("Contrats Entreprise");
    public static final String TITLE_LITIGE = GuiTitle.of("Litiges");

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private ContractListGUI() {}

    public static void openMy(Player p) {
        open(p, TITLE_MY, ContractManager.getByClient(p));
    }

    public static void openBusiness(Player p) {
        Business business = BusinessManager.getMemberBusiness(p.getUniqueId());
        open(p, TITLE_BUSINESS, ContractManager.getByBusiness(business));
    }

    public static void openLitiges(Player p) {
        open(p, TITLE_LITIGE, ContractManager.getLitiges());
    }

    private static void open(Player p, String title, List<Contract> list) {

        Inventory inv = Bukkit.createInventory(null, 54, title);
        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fContrats §6✦")
                        .lore(
                                "§8• §7Total : §e" + list.size(),
                                "§8• §7Argent bloqué",
                                "§8• §7Validation ou litige",
                                "",
                                "§e➜ §fSélectionnez un contrat"
                        )
                        .build()
        );

        int index = 0;

        for (Contract contract : list) {
            if (index >= SLOTS.length) break;

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(Material.PAPER)
                            .name("§6✦ §f" + shortText(contract.getTitle(), 22) + " §6✦")
                            .lore(
                                    "§8• §7Client : §e" + shortText(contract.getClientName(), 14),
                                    "§8• §7Entreprise : §b" + shortText(contract.getBusinessName(), 14),
                                    "§8• §7État : " + contract.getStatus().getDisplayName(),
                                    "§8• §7Budget : §e" + VaultHook.format(contract.getGrossAmount()),
                                    "§8• §7Délai : §f" + shortDate(contract.getDueAt()),
                                    "",
                                    "§e➜ §fOuvrir"
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
                        .name("§6✦ §fRetour §6✦")
                        .lore("§8• §7Menu contrats")
                        .action("open_contracts")
                        .build()
        );

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
