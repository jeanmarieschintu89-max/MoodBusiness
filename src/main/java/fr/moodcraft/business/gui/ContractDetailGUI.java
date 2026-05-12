package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public final class ContractDetailGUI {

    public static final String TITLE =
            "§8✦ §6Contrat §8✦";

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
                        .name("§6✦ §f" + shortText(contract.getTitle(), 22))
                        .lore(
                                "§7Client: §e" + shortText(contract.getClientName(), 14),
                                "§7Entreprise: §b" + shortText(contract.getBusinessName(), 14),
                                "§7Statut: " + contract.getStatus().getDisplayName(),
                                "",
                                "§7Brut: §e" + VaultHook.format(contract.getGrossAmount()),
                                "§7Taxe: §c" + VaultHook.format(contract.getTaxAmount())
                                        + " §8(" + shortPercent(contract.getTaxRate()) + "%)",
                                "§7Net: §a" + VaultHook.format(contract.getNetAmount()),
                                "§7Bloqué: §e" + VaultHook.format(contract.getEscrowAmount()),
                                "",
                                "§7Fin: §f" + shortDate(contract.getDueAt()),
                                "§7Validation: §f" + shortDate(contract.getValidateBefore()),
                                "",
                                "§8ID: §7" + shortText(contract.getId(), 24)
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
                            .name("§6✦ §fTerminer §6✦")
                            .lore(
                                    "§7Marquer le travail",
                                    "§7comme terminé.",
                                    "",
                                    "§8• §7Commentaire chat",
                                    "§a✔ Entreprise"
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
                            .name("§6✦ §fValider §6✦")
                            .lore(
                                    "§7Confirmer le travail.",
                                    "",
                                    "§7Taxe: §c" + VaultHook.format(contract.getTaxAmount()),
                                    "§7Net: §a" + VaultHook.format(contract.getNetAmount()),
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
                            .name("§6✦ §fLitige §6✦")
                            .lore(
                                    "§7Signaler un problème.",
                                    "",
                                    "§8• §7Fonds bloqués",
                                    "§8• §7Décision staff",
                                    "",
                                    "§c⚖ Procédure"
                            )
                            .action("contract_litige_chat")
                            .target(contract.getId())
                            .build()
            );
        }

        if (p.hasPermission("moodbusiness.staff.litige")
                && contract.getStatus() == ContractStatus.LITIGE) {

            SafeGUI.set(
                    inv,
                    40,
                    new ItemBuilder(Material.NETHER_STAR)
                            .name("§6✦ §fDécision staff §6✦")
                            .lore(
                                    "§7Résoudre le litige.",
                                    "",
                                    "§8• §7Payer entreprise",
                                    "§8• §7Rembourser client",
                                    "",
                                    "§cAccès admin"
                            )
                            .action("contract_admin_resolve")
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
                                "§7Menu contrats"
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
                        history.size() - 6
                );

        List<String> lore =
                new ArrayList<>();

        for (int i = start; i < history.size(); i++) {

            lore.add(
                    "§7" + shortText(
                            history.get(i),
                            34
                    )
            );
        }

        return lore.toArray(
                new String[0]
        );
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Aucun";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private static String shortDate(
            long time
    ) {

        String date =
                TimeUtil.formatDate(time);

        if (date == null || date.equalsIgnoreCase("Jamais")) {
            return "Aucune";
        }

        if (date.length() <= 10) {
            return date;
        }

        return date.substring(0, 10);
    }

    private static String shortPercent(
            double value
    ) {

        if (value == Math.rint(value)) {
            return String.valueOf((int) value);
        }

        return String.format("%.1f", value)
                .replace(",", ".");
    }
}