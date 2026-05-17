package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessBankManager;
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

    public static final String TITLE = GuiTitle.of("Argent entreprise");

    private BusinessBankGUI() {}

    public static void open(Player p, Business business) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        boolean canManage = BusinessBankManager.canManageBank(p, business);
        int historySize = FinanceStorage.getByBusiness(business.getId()).size();

        SafeGUI.set(inv, 4, new ItemBuilder(Material.GOLD_INGOT)
                .name("§6✦ §fArgent de l'entreprise §6✦")
                .lore(
                        "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                        "§8• §7Solde : §e" + VaultHook.format(business.getBalance()),
                        "§8• §7Mouvements : §e" + historySize,
                        "",
                        "§e➜ §fBanque simple de l'entreprise"
                )
                .build());

        SafeGUI.set(inv, 19, new ItemBuilder(Material.EMERALD)
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
                .build());

        SafeGUI.set(inv, 21, new ItemBuilder(canManage ? Material.REDSTONE : Material.GRAY_DYE)
                .name("§6✦ §fRetirer §6✦")
                .lore(
                        "§8• §7Retire de l'argent",
                        "§8• §7vers ton compte joueur",
                        "§8• §7Patron, manager ou trésorier",
                        "",
                        canManage ? "§e➜ §fSaisir le montant" : "§c✖ §fAccès limité"
                )
                .action(canManage ? "bank_withdraw_chat" : "coming_soon")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 23, new ItemBuilder(canManage ? Material.SUNFLOWER : Material.GRAY_DYE)
                .name("§6✦ §fPrime §6✦")
                .lore(
                        "§8• §7Verse une prime à un membre",
                        "§8• §7Pseudo puis montant dans le chat",
                        "§8• §7Paiement immédiat",
                        "",
                        canManage ? "§e➜ §fSaisir la prime" : "§c✖ §fAccès limité"
                )
                .action(canManage ? "bank_prime_chat" : "coming_soon")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 31, new ItemBuilder(Material.BOOK)
                .name("§6✦ §fHistorique §6✦")
                .lore(
                        "§8• §7Dépôts",
                        "§8• §7Retraits",
                        "§8• §7Primes",
                        "§8• §7Missions validées",
                        "§8• §7Lignes : §e" + historySize,
                        "",
                        "§e➜ §fOuvrir l'historique"
                )
                .action("open_finance_history")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 40, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Mon entreprise")
                .action("back_business_main")
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
