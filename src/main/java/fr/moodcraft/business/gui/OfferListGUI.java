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
            "§8✦ §6Offres Reçues §8✦";

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
                        .name("§6✦ §f" + request.getTitle() + " §6✦")
                        .lore(
                                "§7Offres reçues: §e" + offers.size(),
                                "§7Budget demandé: §e" + VaultHook.format(request.getBudget()),
                                "",
                                "§8• §7Accepter une offre créera",
                                "§8• §7le contrat sécurisé au Pack 6."
                        )
                        .build()
        );

        int index = 0;

        for (Offer offer : offers) {

            if (index >= SLOTS.length) {
                break;
            }

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §f" + offer.getBusinessName() + " §6✦")
                            .lore(
                                    "§7Envoyée par: §e" + offer.getSenderName(),
                                    "§7Montant: §e" + VaultHook.format(offer.getAmount()),
                                    "§7Délai proposé: §b" + offer.getDueDays() + " jours",
                                    "§7Statut: " + offer.getStatus().getDisplayName(),
                                    "§7Date: §f" + TimeUtil.formatDate(offer.getCreatedAt()),
                                    "",
                                    "§6✦ §fCommentaire",
                                    "§7" + crop(offer.getComment()),
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
                                "§7Revenir au dossier de demande."
                        )
                        .action("request_detail")
                        .target(request.getId())
                        .build()
        );

        p.openInventory(inv);
    }

    private static String crop(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return "Aucun commentaire.";
        }

        if (text.length() <= 45) {
            return text;
        }

        return text.substring(0, 45) + "...";
    }
}