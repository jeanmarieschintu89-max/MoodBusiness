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
            "§6✦ §8Contrat §6✦";

    private ContractDetailGUI() {}

    public static void open(
            Player p,
            Contract contract
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        45,
                        TITLE
                );

        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §f" + shortText(contract.getTitle(), 22) + " §6✦")
                        .lore(
                                "§8• §7Client : §e" + shortText(contract.getClientName(), 14),
                                "§8• §7Entreprise : §b" + shortText(contract.getBusinessName(), 14),
                                "§8• §7Responsable : §f" + shortText(contract.getBusinessActorName(), 14),
                                "§8• §7État : " + contract.getStatus().getDisplayName(),
                                "",
                                "§8• §7Budget : §e" + VaultHook.format(contract.getGrossAmount()),
                                "§8• §7Taxe : §c" + VaultHook.format(contract.getTaxAmount())
                                        + " §8(" + shortPercent(contract.getTaxRate()) + "%§8)",
                                "§8• §7Net entreprise : §a" + VaultHook.format(contract.getNetAmount()),
                                "§8• §7Argent bloqué : §e" + VaultHook.format(contract.getEscrowAmount()),
                                "",
                                "§8• §7Délai : §f" + shortDate(contract.getDueAt()),
                                "§8• §7Validation : §f" + shortDate(contract.getValidateBefore()),
                                "§8• §7ID : §8" + shortText(contract.getId(), 24)
                        )
                        .build()
        );

        if (ContractManager.canBusinessComplete(
                p,
                contract
        )) {

            SafeGUI.set(
                    inv,
                    19,
                    new ItemBuilder(Material.LIME_DYE)
                            .name("§6✦ §fTerminer §6✦")
                            .lore(
                                    "§8• §7Marquer le travail terminé",
                                    "§8• §7Le client pourra valider",
                                    "",
                                    "§a✔ §fAction entreprise"
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
                    21,
                    new ItemBuilder(Material.GOLD_INGOT)
                            .name("§6✦ §fValider §6✦")
                            .lore(
                                    "§8• §7Confirmer le travail",
                                    "§8• §7Verse l'argent à l'entreprise",
                                    "",
                                    "§8• §7Taxe : §c" + VaultHook.format(contract.getTaxAmount()),
                                    "§8• §7Net : §a" + VaultHook.format(contract.getNetAmount()),
                                    "",
                                    "§a✔ §fPaiement final"
                            )
                            .action("contract_validate")
                            .target(contract.getId())
                            .build()
            );
        }

        if (contract.getStatus().isOpen()) {

            SafeGUI.set(
                    inv,
                    23,
                    new ItemBuilder(Material.ANVIL)
                            .name("§6✦ §fLitige §6✦")
                            .lore(
                                    "§8• §7Signaler un problème",
                                    "§8• §7L'argent reste bloqué",
                                    "§8• §7Décision staff possible",
                                    "",
                                    "§c✖ §fProcédure"
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
                    25,
                    new ItemBuilder(Material.NETHER_STAR)
                            .name("§6✦ §fDécision staff §6✦")
                            .lore(
                                    "§8• §7Résoudre le litige",
                                    "§8• §7Payer ou rembourser",
                                    "",
                                    "§c✖ §fAccès staff"
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
                40,
                new ItemBuilder(Material.ARROW)
                        .name("§6✦ §fRetour §6✦")
                        .lore(
                                "§8• §7Menu contrats"
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
                    "§8• §7Aucun historique."
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
                    "§8• §7" + shortText(
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
