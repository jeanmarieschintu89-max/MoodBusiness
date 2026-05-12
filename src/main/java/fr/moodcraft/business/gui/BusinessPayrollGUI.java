package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessBankManager;
import fr.moodcraft.business.manager.PayrollManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.storage.PayrollStorage;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessPayrollGUI {

    public static final String TITLE =
            "§8✦ §6Paie Entreprise §8✦";

    private BusinessPayrollGUI() {}

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

        boolean canConfig =
                BusinessBankManager.canConfigurePayroll(
                        p,
                        business
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.CLOCK)
                        .name("§6✦ §fPaie mensuelle §6✦")
                        .lore(
                                "§7Entreprise: §e" + business.getName(),
                                "§7Solde: §e" + VaultHook.format(business.getBalance()),
                                "§7Total mensuel estimé: §e"
                                        + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(
                                                business
                                        )
                                ),
                                "",
                                "§7Configuration: "
                                        + (canConfig
                                        ? "§aDirigeant"
                                        : "§cLecture seule"),
                                "",
                                "§8• §7Les salaires sont versés",
                                "§8• §7une fois par mois."
                        )
                        .build()
        );

        int slot = 10;

        for (BusinessRole role : BusinessRole.values()) {

            double salary =
                    PayrollStorage.getSalary(
                            business.getId(),
                            role
                    );

            Material icon =
                    switch (role) {

                        case DIRIGEANT -> Material.NETHER_STAR;
                        case GERANT -> Material.GOLDEN_HELMET;
                        case RESPONSABLE_CONTRATS -> Material.WRITABLE_BOOK;
                        case TRESORIER -> Material.GOLD_INGOT;
                        case EMPLOYE -> Material.IRON_PICKAXE;
                        case APPRENTI -> Material.COPPER_INGOT;
                        case STAGIAIRE -> Material.PAPER;
                    };

            SafeGUI.set(
                    inv,
                    slot,
                    new ItemBuilder(icon)
                            .name("§6✦ " + role.getDisplayName() + " §6✦")
                            .lore(
                                    "§7Salaire mensuel: §e"
                                            + VaultHook.format(salary),
                                    "",
                                    canConfig
                                            ? "§a✔ Clique pour modifier"
                                            : "§cRéservé au dirigeant",
                                    "",
                                    "§8• §7Le montant sera saisi",
                                    "§8• §7directement dans le chat."
                            )
                            .action(
                                    canConfig
                                            ? "payroll_salary_input"
                                            : "coming_soon"
                            )
                            .target(business.getId() + ":" + role.name())
                            .build()
            );

            slot++;

            if (slot == 17) {
                slot = 19;
            }

            if (slot == 26) {
                slot = 28;
            }

            if (slot == 35) {
                slot = 37;
            }
        }

        SafeGUI.set(
                inv,
                45,
                new ItemBuilder(Material.EMERALD)
                        .name("§6✦ §fLancer la paie §6✦")
                        .lore(
                                "§7Verse manuellement la paie mensuelle.",
                                "§7Utile pour tester ou rattraper",
                                "§7une paie bloquée.",
                                "",
                                canConfig
                                        ? "§a✔ Lancer maintenant"
                                        : "§cRéservé au dirigeant",
                                "",
                                "§8• §7Commande alternative:",
                                "§f/entreprise paie"
                        )
                        .action(
                                canConfig
                                        ? "payroll_run_help"
                                        : "coming_soon"
                        )
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir à la banque entreprise."
                        )
                        .action("open_bank")
                        .target(business.getId())
                        .build()
        );

        p.openInventory(inv);
    }
}