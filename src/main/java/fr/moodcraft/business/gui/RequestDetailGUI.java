package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class RequestDetailGUI {

    public static final String TITLE =
            "§6✦ §8Demande §6✦";

    private RequestDetailGUI() {}

    public static void open(
            Player p,
            BusinessRequest request
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
                        .name("§6✦ §f" + shortText(request.getTitle(), 24) + " §6✦")
                        .lore(
                                "§8• §7Auteur : §e" + shortText(request.getCreatorName(), 14),
                                "§8• §7Type : " + request.getCategory().getDisplayName(),
                                "§8• §7Budget : §e" + VaultHook.format(request.getBudget()),
                                "§8• §7Délai : §b" + request.getDueDays() + "j",
                                "§8• §7État : " + request.getStatus().getDisplayName(),
                                "§8• §7Créée : §f" + shortDate(request.getCreatedAt()),
                                "",
                                "§6✦ §fDescription §6✦",
                                "§8• §7" + shortText(request.getDescription(), 32)
                        )
                        .build()
        );

        Business business =
                BusinessManager.getMemberBusiness(
                        p.getUniqueId()
                );

        if (business != null
                && BusinessManager.canManageContracts(
                p,
                business
        )
                && request.getStatus().isOpen()
                && !request.getCreatorUuid().equals(
                p.getUniqueId()
        )) {

            SafeGUI.set(
                    inv,
                    20,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fPrendre en charge §6✦")
                            .lore(
                                    "§8• §7Entreprise : §e" + shortText(business.getName(), 16),
                                    "§8• §7Budget client : §e" + VaultHook.format(request.getBudget()),
                                    "§8• §7Délai : §b" + request.getDueDays() + "j",
                                    "",
                                    "§a✔ §fCrée le contrat directement",
                                    "§8• §7L'argent du client sera bloqué"
                            )
                            .action("request_take")
                            .target(request.getId())
                            .build()
            );
        }

        if (request.getCreatorUuid().equals(
                p.getUniqueId()
        )) {

            SafeGUI.set(
                    inv,
                    20,
                    new ItemBuilder(Material.CHEST)
                            .name("§6✦ §fSuivi de la demande §6✦")
                            .lore(
                                    "§8• §7Une entreprise peut la prendre",
                                    "§8• §7Contrat créé automatiquement",
                                    "§8• §7Argent bloqué à la prise en charge",
                                    "",
                                    "§e➜ §fAucune offre à accepter"
                            )
                            .build()
            );

            if (request.getStatus().isOpen()) {

                SafeGUI.set(
                        inv,
                        24,
                        new ItemBuilder(Material.REDSTONE_BLOCK)
                                .name("§c✦ §fAnnuler la demande §c✦")
                                .lore(
                                        "§8• §7Ferme cette demande",
                                        "§8• §7Aucun contrat ne sera créé",
                                        "",
                                        "§c✖ §fClique pour annuler"
                                )
                                .action("request_cancel")
                                .target(request.getId())
                                .build()
                );
            }
        }

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.ARROW)
                        .name("§6✦ §fRetour §6✦")
                        .lore(
                                "§8• §7Menu demandes"
                        )
                        .action("open_requests")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Non renseigné";
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
}