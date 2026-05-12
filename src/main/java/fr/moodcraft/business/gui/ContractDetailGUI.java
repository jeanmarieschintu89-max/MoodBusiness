package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class ContractDetailGUI {

    public static final String TITLE =
            "§8✦ §6Dossier Contrat §8✦";

    private ContractDetailGUI() {}

    public static void open(
            Player p,
            Contract contract
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §f" + contract.getTitle() + " §6✦")
                        .lore(
                                "§7Client: §e" + contract.getClientName(),
                                "§7Entreprise: §b" + contract.getBusinessName(),
                                "§7Statut: " + contract.getStatus().getDisplayName(),
                                "",
                                "§7Montant brut: §e" + VaultHook.format(contract.getGrossAmount()),
                                "§7Taxe économique: §c" + VaultHook.format(contract.getTaxAmount())
                                        + " §8(" + contract.getTaxRate() + "%)",
                                "§7Versement entreprise: §a" + VaultHook.format(contract.getNetAmount()),
                                "§7Fonds bloqués: §e" + VaultHook.format(contract.getEscrowAmount()),
                                "",
                                "§7Échéance: §f" + TimeUtil.formatDate(contract.getDueAt()),
                                "§7Validation avant: §f" + TimeUtil.formatDate(contract.getValidateBefore()),
                                "",
                                "§8• §7ID: §8" + contract.getId()
                        )
                        .build()
        );

        if (ContractManager.canBusinessComplete(
                p,
                contract
        )) {

            SafeGUI.set(
                    inv,
                    20,
                    new ItemBuilder(Material.LIME_DYE)
                            .name("§6✦ §fMarquer terminé §6✦")
                            .lore(
                                    "§7Indiquer que le travail est terminé.",
                                    "§7Un commentaire sera demandé",
                                    "§7dans le chat.",
                                    "",
                                    "§a✔ Finalisation entreprise"
                            )
                            .action("contract_complete_chat")
                            .target(contract.getId())
                            .build()
            );
        }

        if (ContractManager.canClientValidate(
                p,
                contract
        )) {

            SafeGUI.set(
                    inv,
                    22,
                    new ItemBuilder(Material.GOLD_INGOT)
                            .name("§6✦ §fValider le contrat §6✦")
                            .lore(
                                    "§7Confirmer que le travail est conforme.",
                                    "§7Les fonds seront versés à l'entreprise.",
                                    "",
                                    "§7Taxe: §c" + VaultHook.format(contract.getTaxAmount()),
                                    "§7Net entreprise: §a" + VaultHook.format(contract.getNetAmount()),
                                    "",
                                    "§a✔ Paiement final"
                            )
                            .action("contract_validate")
                            .target(contract.getId())
                            .build()
            );
        }

        if (contract.getStatus().isOpen()) {

            SafeGUI.set(
                    inv,
                    24,
                    new ItemBuilder(Material.ANVIL)
                            .name("§6✦ §fOuvrir un litige §6✦")
                            .lore(
                                    "§7Signaler un problème sur ce contrat.",
                                    "§7Les fonds resteront bloqués",
                                    "§7jusqu'à décision administrative.",
                                    "",
                                    "§c⚖ Procédure sensible"
                            )
                            .action("contract_litige_chat")
                            .target(contract.getId())
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fHistorique §6✦")
                        .lore(
                                historyLore(contract)
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir au menu contrats."
                        )
                        .action("open_contracts")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String[] historyLore(
            Contract contract
    ) {

        List<String> history =
                contract.getHistory();

        if (history.isEmpty()) {

            return new String[]{
                    "§7Aucun historique."
            };
        }

        int start =
                Math.max(
                        0,
                        history.size() - 8
                );

        String[] lore =
                new String[history.size() - start];

        int index = 0;

        for (int i = start; i < history.size(); i++) {

            lore[index] =
                    history.get(i);

            index++;
        }

        return lore;
    }
}