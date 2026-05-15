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

    public static final String TITLE = GuiTitle.of("Salaires");

    private static final int[] ROLE_SLOTS = {
            10, 11, 12, 14, 15, 16, 22
    };

    private BusinessPayrollGUI() {}

    public static void open(Player p, Business business) {

        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        boolean canConfig = BusinessBankManager.canConfigurePayroll(p, business);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.CLOCK)
                .name("§6✦ §fSalaires mensuels §6✦")
                .lore(
                        "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                        "§8• §7Solde : §e" + VaultHook.format(business.getBalance()),
                        "§8• §7Total/mois : §e" + VaultHook.format(PayrollManager.calculateTotalPayroll(business)),
                        "§8• §7Mode : " + (canConfig ? "§aédition" : "§7lecture seule"),
                        "",
                        "§e➜ §fClique un rôle pour modifier"
                )
                .build());

        BusinessRole[] roles = BusinessRole.values();

        for (int i = 0; i < roles.length && i < ROLE_SLOTS.length; i++) {
            BusinessRole role = roles[i];
            double salary = PayrollStorage.getSalary(business.getId(), role);

            Material icon = switch (role) {
                case DIRIGEANT -> Material.NETHER_STAR;
                case GERANT -> Material.GOLDEN_HELMET;
                case RESPONSABLE_CONTRATS -> Material.WRITABLE_BOOK;
                case TRESORIER -> Material.GOLD_INGOT;
                case EMPLOYE -> Material.IRON_PICKAXE;
                case APPRENTI -> Material.COPPER_INGOT;
                case STAGIAIRE -> Material.PAPER;
            };

            SafeGUI.set(inv, ROLE_SLOTS[i], new ItemBuilder(canConfig ? icon : Material.GRAY_DYE)
                    .name("§6✦ " + role.getDisplayName() + " §6✦")
                    .lore(
                            "§8• §7Salaire/mois : §e" + VaultHook.format(salary),
                            "§8• §7Rôle : " + role.getDisplayName(),
                            "",
                            canConfig ? "§e➜ §fModifier le montant" : "§c✖ §fRéservé au dirigeant"
                    )
                    .action(canConfig ? "payroll_salary_input" : "coming_soon")
                    .target(business.getId() + ":" + role.name())
                    .build());
        }

        SafeGUI.set(inv, 31, new ItemBuilder(canConfig ? Material.EMERALD_BLOCK : Material.GRAY_DYE)
                .name("§6✦ §fLancer la paie §6✦")
                .lore(
                        "§8• §7Verse les salaires maintenant",
                        "§8• §7Utile pour rattraper une paie",
                        "§8• §7Aucune dette si la banque est vide",
                        "",
                        canConfig ? "§e➜ §fVoir la commande" : "§c✖ §fRéservé au dirigeant"
                )
                .action(canConfig ? "payroll_run_help" : "coming_soon")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 40, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Argent entreprise")
                .action("open_bank")
                .target(business.getId())
                .build());

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
