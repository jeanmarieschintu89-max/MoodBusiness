package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.OfferManager;

import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.Offer;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class OfferListGUI {

    public static final String TITLE =
            "§8✦ §6Offres §8✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private OfferListGUI() {}

    public static void open(
            Player p,
            BusinessRequest request
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        List<Offer> offers =
                OfferManager.getByRequest(
                        request.getId()
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §f" + shortText(request.getTitle(), 22))
                        .lore(
                                "§7Offres: §e" + offers.size(),
                                "§7Budget: §e" + VaultHook.format(request.getBudget()),
                                "",
                                "§8• §7Accepter une offre",
                                "§8• §7créera un contrat."
                        )
                        .build()
        );

        int index = 0;

        for (Offer offer : offers) {

            if (index >= SLOTS.length) {
                break;
            }

            Material icon =
                    offer.getStatus().isActive()
                            ? Material.EMERALD
                            : Material.GRAY_DYE;

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(icon)
                            .name("§6✦ §f" + shortText(offer.getBusinessName(), 18))
                            .lore(
                                    "§7Auteur: §e" + shortText(offer.getSenderName(), 14),
                                    "§7Montant: §e" + VaultHook.format(offer.getAmount()),
                                    "§7Délai: §b" + offer.getDueDays() + "j",
                                    "§7Statut: " + offer.getStatus().getDisplayName(),
                                    "§7Date: §f" + shortDate(offer.getCreatedAt()),
                                    "",
                                    "§7Note: §f" + shortText(offer.getComment(), 26),
                                    "",
                                    offer.getStatus().isActive()
                                            ? "§a✔ Clique pour accepter"
                                            : "§8• §7Offre clôturée"
                            )
                            .action(
                                    offer.getStatus().isActive()
                                            ? "offer_accept"
                                            : "coming_soon"
                            )
                            .target(offer.getId())
                            .build()
            );

            index++;
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Dossier demande"
                        )
                        .action("request_detail")
                        .target(request.getId())
                        .build()
        );

        p.openInventory(inv);
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
}