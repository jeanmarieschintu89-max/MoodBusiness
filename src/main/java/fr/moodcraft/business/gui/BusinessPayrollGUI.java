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
            "§6✦ §8Paie Entreprise §6✦";

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
                                "§7Salaires de l'entreprise.",
                                "",
                                "§7Entreprise: §e" + shortText(business.getName(), 18),
                                "§7Solde: §e" + VaultHook.format(business.getBalance()),
                                "§7Total/mois: §e"
                                        + VaultHook.format(
                                        PayrollManager.calculateTotalPayroll(
                                                business
                                        )
                                ),
                                "",
                                "§7Mode: "
                                        + (canConfig
                                        ? "§aDirigeant"
                                        : "§cLecture seule"),
                                "",
                                "§8• §7Versée chaque mois",
                                "§8• §7Pas de dette automatique"
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
                                    "§7Salaire/mois: §e"
                                            + VaultHook.format(salary),
                                    "",
                                    canConfig
                                            ? "§a✔ Clique pour modifier"
                                            : "§cRéservé au dirigeant",
                                    "",
                                    "§8• §7Montant dans le chat"
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
                                "§7Verse les salaires",
                                "§7maintenant.",
                                "",
                                "§8• §7Utile pour tester",
                                "§8• §7ou rattraper une paie",
                                "",
                                canConfig
                                        ? "§a✔ Lancer"
                                        : "§cRéservé au dirigeant"
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
                                "§7Banque entreprise"
                        )
                        .action("open_bank")
                        .target(business.getId())
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
