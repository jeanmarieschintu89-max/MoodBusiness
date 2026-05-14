package fr.moodcraft.business.gui;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.BusinessBankManager;
import fr.moodcraft.business.manager.PayrollManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.storage.FinanceStorage;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessBankGUI {

    public static final String TITLE =
            "§6✦ §8Argent entreprise §6✦";

    private BusinessBankGUI() {}

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

        boolean canManage =
                BusinessBankManager.canManageBank(
                        p,
                        business
                );

        int historySize =
                FinanceStorage.getByBusiness(
                        business.getId()
                ).size();

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§6✦ §fArgent de l'entreprise §6✦")
                        .lore(
                                "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                                "§8• §7Solde : §e" + VaultHook.format(business.getBalance()),
                                "§8• §7Paie/mois : §e" + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(business)
                                ),
                                "§8• §7Logs : §e" + historySize,
                                "",
                                "§e➜ §fBanque séparée du joueur"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                19,
                new ItemBuilder(Material.EMERALD)
                        .name("§6✦ §fDéposer §6✦")
                        .lore(
                                "§8• §7Ajoute ton argent liquide",
                                "§8• §7à la banque entreprise",
                                "§8• §7Exemple : §e5000",
                                "",
                                "§e➜ §fSaisir le montant"
                        )
                        .action("bank_deposit_chat")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                21,
                new ItemBuilder(
                        canManage
                                ? Material.REDSTONE
                                : Material.GRAY_DYE
                )
                        .name("§6✦ §fRetirer §6✦")
                        .lore(
                                "§8• §7Retire de l'argent",
                                "§8• §7vers ton compte joueur",
                                "§8• §7Dirigeant, gérant ou trésorier",
                                "",
                                canManage
                                        ? "§e➜ §fSaisir le montant"
                                        : "§c✖ §fAccès limité"
                        )
                        .action(
                                canManage
                                        ? "bank_withdraw_chat"
                                        : "coming_soon"
                        )
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                23,
                new ItemBuilder(
                        canManage
                                ? Material.SUNFLOWER
                                : Material.GRAY_DYE
                )
                        .name("§6✦ §fPrime §6✦")
                        .lore(
                                "§8• §7Verse une prime à un membre",
                                "§8• §7Pseudo puis montant dans le chat",
                                "§8• §7Confirmation si montant élevé",
                                "",
                                canManage
                                        ? "§e➜ §fSaisir la prime"
                                        : "§c✖ §fAccès limité"
                        )
                        .action(
                                canManage
                                        ? "bank_prime_chat"
                                        : "coming_soon"
                        )
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                25,
                new ItemBuilder(Material.CLOCK)
                        .name("§6✦ §fSalaires §6✦")
                        .lore(
                                "§8• §7Paie mensuelle",
                                "§8• §7Salaire par rôle",
                                "§8• §7Jour de paie : §e"
                                        + Main.getInstance().getConfig().getInt(
                                        "payroll.default-payday",
                                        1
                                ),
                                "",
                                "§e➜ §fOuvrir les salaires"
                        )
                        .action("open_payroll")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fLogs financiers §6✦")
                        .lore(
                                "§8• §7Dépôts",
                                "§8• §7Retraits",
                                "§8• §7Primes",
                                "§8• §7Paie et contrats",
                                "§8• §7Lignes : §e" + historySize,
                                "",
                                "§e➜ §fOuvrir les logs"
                        )
                        .action("open_finance_history")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.ARROW)
                        .name("§6✦ §fRetour §6✦")
                        .lore(
                                "§8• §7Mon entreprise"
                        )
                        .action("back_business_main")
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
}
