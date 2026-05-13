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

    public static final String TITLE =
            "§6✦ §8Décision Litige §6✦";

    private ContractAdminResolveGUI() {}

    public static void open(
            Player p,
            Contract contract
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        27,
                        TITLE
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.ANVIL)
                        .name("§6✦ §fLitige §6✦")
                        .lore(
                                "§7Contrat: §e" + shortText(contract.getTitle(), 18),
                                "§7Client: §e" + shortText(contract.getClientName(), 14),
                                "§7Entreprise: §b" + shortText(contract.getBusinessName(), 14),
                                "",
                                "§7Argent bloqué: §e" + VaultHook.format(contract.getEscrowAmount()),
                                "§7Brut: §e" + VaultHook.format(contract.getGrossAmount()),
                                "§7Taxe: §c" + VaultHook.format(contract.getTaxAmount()),
                                "§7Net entreprise: §a" + VaultHook.format(contract.getNetAmount()),
                                "",
                                "§cUn staff doit choisir."
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                11,
                new ItemBuilder(Material.EMERALD_BLOCK)
                        .name("§a✔ Payer l’entreprise")
                        .lore(
                                "§7Donne l'argent net",
                                "§7à l'entreprise.",
                                "",
                                "§7Versement: §a" + VaultHook.format(contract.getNetAmount()),
                                "§7Taxe: §c" + VaultHook.format(contract.getTaxAmount()),
                                "",
                                "§8• §7Le contrat sera validé",
                                "§8• §7Le litige sera fermé",
                                "",
                                "§aClique pour choisir"
                        )
                        .action("contract_admin_pay_business")
                        .target(contract.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                15,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§e✔ Rembourser le client")
                        .lore(
                                "§7Rend l'argent bloqué",
                                "§7au client.",
                                "",
                                "§7Remboursement: §e" + VaultHook.format(contract.getEscrowAmount()),
                                "",
                                "§8• §7Le contrat sera annulé",
                                "§8• §7Le litige sera fermé",
                                "",
                                "§eClique pour choisir"
                        )
                        .action("contract_admin_refund_client")
                        .target(contract.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Dossier du contrat"
                        )
                        .action("contract_detail")
                        .target(contract.getId())
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
