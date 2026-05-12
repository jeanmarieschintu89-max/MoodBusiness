package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.OfferManager;

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
            "§8✦ §6Demande §8✦";

    private RequestDetailGUI() {}

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

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §f" + shortText(request.getTitle(), 24))
                        .lore(
                                "§7Auteur: §e" + shortText(request.getCreatorName(), 14),
                                "§7Type: " + request.getCategory().getDisplayName(),
                                "§7Budget: §e" + VaultHook.format(request.getBudget()),
                                "§7Délai: §b" + request.getDueDays() + "j",
                                "§7Statut: " + request.getStatus().getDisplayName(),
                                "§7Créée: §f" + shortDate(request.getCreatedAt()),
                                "",
                                "§6✦ §fDescription",
                                "§7" + shortText(request.getDescription(), 32)
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

            boolean hasOffer =
                    OfferManager.hasActiveOffer(
                            request.getId(),
                            business.getId()
                    );

            SafeGUI.set(
                    inv,
                    21,
                    new ItemBuilder(
                            hasOffer
                                    ? Material.GRAY_DYE
                                    : Material.EMERALD
                    )
                            .name("§6✦ §fProposer une offre §6✦")
                            .lore(
                                    "§7Entreprise: §e" + shortText(business.getName(), 16),
                                    "",
                                    hasOffer
                                            ? "§cOffre déjà envoyée"
                                            : "§a✔ Formulaire guidé",
                                    "",
                                    "§8• §7Montant",
                                    "§8• §7Délai",
                                    "§8• §7Commentaire"
                            )
                            .action(
                                    hasOffer
                                            ? "coming_soon"
                                            : "offer_start"
                            )
                            .target(request.getId())
                            .build()
            );
        }

        if (request.getCreatorUuid().equals(
                p.getUniqueId()
        )) {

            SafeGUI.set(
                    inv,
                    23,
                    new ItemBuilder(Material.CHEST)
                            .name("§6✦ §fOffres reçues §6✦")
                            .lore(
                                    "§7Voir les propositions",
                                    "§7des entreprises.",
                                    "",
                                    "§eClique pour ouvrir"
                            )
                            .action("offer_list")
                            .target(request.getId())
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Menu demandes"
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