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
            "§8✦ §6Dossier Demande §8✦";

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
                        .name("§6✦ §f" + request.getTitle() + " §6✦")
                        .lore(
                                "§7Demandeur: §e" + request.getCreatorName(),
                                "§7Catégorie: " + request.getCategory().getDisplayName(),
                                "§7Budget: §e" + VaultHook.format(request.getBudget()),
                                "§7Délai souhaité: §b" + request.getDueDays() + " jours",
                                "§7Statut: " + request.getStatus().getDisplayName(),
                                "§7Créée le: §f" + TimeUtil.formatDate(request.getCreatedAt()),
                                "",
                                "§6✦ §fDescription",
                                "§7" + crop(request.getDescription())
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
                    21,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fProposer une offre §6✦")
                            .lore(
                                    "§7Entreprise: §e" + business.getName(),
                                    "§7Envoyer un montant, un délai",
                                    "§7et un commentaire au demandeur.",
                                    "",
                                    OfferManager.hasActiveOffer(
                                            request.getId(),
                                            business.getId()
                                    )
                                            ? "§cOffre déjà envoyée"
                                            : "§a✔ Formulaire guidé"
                            )
                            .action(
                                    OfferManager.hasActiveOffer(
                                            request.getId(),
                                            business.getId()
                                    )
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
                                    "§7Voir les propositions envoyées",
                                    "§7par les entreprises.",
                                    "",
                                    "§eClique pour consulter"
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
                                "§7Revenir aux demandes."
                        )
                        .action("open_requests")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String crop(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return "Non renseigné.";
        }

        if (text.length() <= 80) {
            return text;
        }

        return text.substring(0, 80) + "...";
    }
}