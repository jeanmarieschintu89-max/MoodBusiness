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
            "§8✦ §6Décision Litige §8✦";

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
                        .name("§6✦ §fLitige économique §6✦")
                        .lore(
                                "§7Contrat: §e" + contract.getTitle(),
                                "§7Client: §e" + contract.getClientName(),
                                "§7Entreprise: §b" + contract.getBusinessName(),
                                "",
                                "§7Fonds bloqués: §e" + VaultHook.format(contract.getEscrowAmount()),
                                "§7Montant brut: §e" + VaultHook.format(contract.getGrossAmount()),
                                "§7Taxe prévue: §c" + VaultHook.format(contract.getTaxAmount()),
                                "§7Net entreprise: §a" + VaultHook.format(contract.getNetAmount()),
                                "",
                                "§cDécision administrative requise."
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                11,
                new ItemBuilder(Material.EMERALD_BLOCK)
                        .name("§a✔ Payer l’entreprise")
                        .lore(
                                "§7Verse le montant net",
                                "§7à la banque de l’entreprise.",
                                "",
                                "§7Versement: §a" + VaultHook.format(contract.getNetAmount()),
                                "§7Taxe: §c" + VaultHook.format(contract.getTaxAmount()),
                                "",
                                "§a▶ Résoudre en faveur de l’entreprise"
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
                                "§7Rend les fonds bloqués",
                                "§7au client du contrat.",
                                "",
                                "§7Remboursement: §e" + VaultHook.format(contract.getEscrowAmount()),
                                "",
                                "§e▶ Résoudre en faveur du client"
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
                                "§7Revenir au dossier du contrat."
                        )
                        .action("contract_detail")
                        .target(contract.getId())
                        .build()
        );

        p.openInventory(inv);
    }
}