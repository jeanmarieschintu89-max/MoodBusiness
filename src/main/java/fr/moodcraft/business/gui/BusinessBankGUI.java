package fr.moodcraft.business.gui;

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
            "§8✦ §6Banque Entreprise §8✦";

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

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§6✦ §f" + business.getName() + " §6✦")
                        .lore(
                                "§7Solde entreprise: §e"
                                        + VaultHook.format(
                                        business.getBalance()
                                ),
                                "§7Paie mensuelle estimée: §e"
                                        + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(
                                                business
                                        )
                                ),
                                "§7Historique financier: §e"
                                        + FinanceStorage.getByBusiness(
                                        business.getId()
                                ).size()
                                        + " ligne(s)",
                                "",
                                "§8• §7Service officiel de §aMood§6Craft§7."
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.EMERALD)
                        .name("§6✦ §fDéposer des fonds §6✦")
                        .lore(
                                "§7Transférer de l'argent personnel",
                                "§7vers la banque entreprise.",
                                "",
                                "§eCommande:",
                                "§f/entreprise depot <montant>",
                                "",
                                "§a✔ Tous les membres peuvent déposer"
                        )
                        .action("bank_deposit_help")
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
                        .name("§6✦ §fRetirer des fonds §6✦")
                        .lore(
                                "§7Retirer depuis la banque entreprise",
                                "§7vers votre compte personnel.",
                                "",
                                "§eCommande:",
                                "§f/entreprise retrait <montant>",
                                "",
                                canManage
                                        ? "§a✔ Autorisé"
                                        : "§cAccès trésorier minimum"
                        )
                        .action("bank_withdraw_help")
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
                        .name("§6✦ §fVerser une prime §6✦")
                        .lore(
                                "§7Récompenser un employé,",
                                "§7un apprenti ou un stagiaire.",
                                "",
                                "§eCommande:",
                                "§f/entreprise prime <joueur> <montant>",
                                "",
                                canManage
                                        ? "§a✔ Autorisé"
                                        : "§cAccès trésorier minimum"
                        )
                        .action("bank_prime_help")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.CLOCK)
                        .name("§6✦ §fPaie mensuelle §6✦")
                        .lore(
                                "§7Configurer les salaires",
                                "§7et lancer une paie manuelle.",
                                "",
                                "§eJour de paie: §f"
                                        + fr.moodcraft.business.Main.getInstance()
                                        .getConfig()
                                        .getInt(
                                                "payroll.default-payday",
                                                1
                                        ),
                                "",
                                "§a✔ Ouvrir"
                        )
                        .action("open_payroll")
                        .target(business.getId())
                        .build()
        );

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