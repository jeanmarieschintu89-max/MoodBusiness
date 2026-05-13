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
            "§6✦ §8Banque Entreprise §6✦";

    private BusinessBankGUI() {}

    public static void open(
            Player p,
            Business business
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
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
                        .name("§6✦ §f" + shortText(business.getName(), 22) + " §6✦")
                        .lore(
                                "§7Argent de l'entreprise.",
                                "",
                                "§7Solde: §e"
                                        + VaultHook.format(
                                        business.getBalance()
                                ),
                                "§7Paie/mois: §e"
                                        + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(
                                                business
                                        )
                                ),
                                "§7Historique: §e" + historySize,
                                "",
                                "§8• §7Banque séparée",
                                "§8• §7de la banque joueur"
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.EMERALD)
                        .name("§6✦ §fDéposer §6✦")
                        .lore(
                                "§7Ajouter de l'argent",
                                "§7dans la banque entreprise.",
                                "",
                                "§8• §7Montant dans le chat",
                                "§8• §7Exemple: §e5000",
                                "",
                                "§a✔ Cliquer pour saisir"
                        )
                        .action("bank_deposit_chat")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(
                        canManage
                                ? Material.REDSTONE
                                : Material.GRAY_DYE
                )
                        .name("§6✦ §fRetirer §6✦")
                        .lore(
                                "§7Retirer de l'argent",
                                "§7de l'entreprise.",
                                "",
                                "§8• §7Montant dans le chat",
                                "§8• §7Dirigeant / gérant",
                                "§8• §7ou trésorier",
                                "",
                                canManage
                                        ? "§a✔ Cliquer pour saisir"
                                        : "§cAccès refusé"
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
                24,
                new ItemBuilder(
                        canManage
                                ? Material.SUNFLOWER
                                : Material.GRAY_DYE
                )
                        .name("§6✦ §fPrime §6✦")
                        .lore(
                                "§7Donner une prime",
                                "§7à un membre.",
                                "",
                                "§8• §7Pseudo dans le chat",
                                "§8• §7Montant dans le chat",
                                "§8• §7Confirmation si élevé",
                                "",
                                canManage
                                        ? "§a✔ Cliquer pour saisir"
                                        : "§cAccès refusé"
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
                31,
                new ItemBuilder(Material.CLOCK)
                        .name("§6✦ §fPaie mensuelle §6✦")
                        .lore(
                                "§7Gérer les salaires",
                                "§7de chaque rôle.",
                                "",
                                "§7Jour de paie: §e"
                                        + Main.getInstance()
                                        .getConfig()
                                        .getInt(
                                                "payroll.default-payday",
                                                1
                                        ),
                                "",
                                "§8• §7Salaire par rôle",
                                "§8• §7Paie une fois par mois",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("open_payroll")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                33,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fHistorique §6✦")
                        .lore(
                                "§7Voir les mouvements",
                                "§7de la banque entreprise.",
                                "",
                                "§7Lignes: §e" + historySize,
                                "",
                                "§8• §7Dépôts",
                                "§8• §7Retraits",
                                "§8• §7Primes",
                                "§8• §7Paie",
                                "§8• §7Contrats",
                                "",
                                "§eClique pour ouvrir"
                        )
                        .action("open_finance_history")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Bureau des Entreprises"
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