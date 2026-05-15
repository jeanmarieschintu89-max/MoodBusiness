package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class ContractAdminResolveGUI {

    public static final String TITLE = GuiTitle.of("Décision Litige");

    private ContractAdminResolveGUI() {}

    public static void open(Player p, Contract contract) {

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        SafeGUI.fill(inv);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.ANVIL)
                .name("§6✦ §fLitige §6✦")
                .lore(
                        "§8• §7Contrat : §e" + shortText(contract.getTitle(), 18),
                        "§8• §7Client : §e" + shortText(contract.getClientName(), 14),
                        "§8• §7Entreprise : §b" + shortText(contract.getBusinessName(), 14),
                        "",
                        "§8• §7Argent bloqué : §e" + VaultHook.format(contract.getEscrowAmount()),
                        "§8• §7Brut : §e" + VaultHook.format(contract.getGrossAmount()),
                        "§8• §7Taxe : §c" + VaultHook.format(contract.getTaxAmount()),
                        "§8• §7Net entreprise : §a" + VaultHook.format(contract.getNetAmount()),
                        "",
                        "§c✖ §fDécision staff requise"
                )
                .build());

        SafeGUI.set(inv, 11, new ItemBuilder(Material.EMERALD_BLOCK)
                .name("§a✦ §fPayer l’entreprise §a✦")
                .lore(
                        "§8• §7Donne l'argent net",
                        "§8• §7à l'entreprise",
                        "",
                        "§8• §7Versement : §a" + VaultHook.format(contract.getNetAmount()),
                        "§8• §7Taxe : §c" + VaultHook.format(contract.getTaxAmount()),
                        "§8• §7Le contrat sera validé",
                        "",
                        "§a✔ §fChoisir"
                )
                .action("contract_admin_pay_business")
                .target(contract.getId())
                .build());

        SafeGUI.set(inv, 15, new ItemBuilder(Material.GOLD_INGOT)
                .name("§e✦ §fRembourser le client §e✦")
                .lore(
                        "§8• §7Rend l'argent bloqué",
                        "§8• §7au client",
                        "",
                        "§8• §7Remboursement : §e" + VaultHook.format(contract.getEscrowAmount()),
                        "§8• §7Le contrat sera annulé",
                        "",
                        "§e➜ §fChoisir"
                )
                .action("contract_admin_refund_client")
                .target(contract.getId())
                .build());

        SafeGUI.set(inv, 22, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Dossier du contrat")
                .action("contract_detail")
                .target(contract.getId())
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
