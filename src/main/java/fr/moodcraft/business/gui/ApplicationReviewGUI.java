package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Application;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class ApplicationReviewGUI {

    public static final String TITLE =
            "§6✦ §8Dossier Candidature §6✦";

    private ApplicationReviewGUI() {}

    public static void open(
            Player p,
            Application application
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
                        .name("§6✦ §f" + shortText(application.getApplicantName(), 18) + " §6✦")
                        .lore(
                                "§7Entreprise: §e" + shortText(application.getBusinessName(), 18),
                                "§7Type: " + application.getType().getDisplayName(),
                                "§7État: " + application.getStatus().getDisplayName(),
                                "§7Créée: §f" + shortDate(application.getCreatedAt()),
                                "",
                                "§6✦ §fPrésentation",
                                "§7" + shortText(application.getPresentation(), 32),
                                "",
                                "§6✦ §fDisponibilités",
                                "§7" + shortText(application.getAvailability(), 32)
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fAccepter stage §6✦")
                        .lore(
                                "§7Le joueur rejoint",
                                "§7comme stagiaire.",
                                "",
                                "§8• §7Accès limité",
                                "§8• §7Découverte",
                                "",
                                "§bClique pour accepter"
                        )
                        .action("application_accept_stage")
                        .target(application.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.COPPER_INGOT)
                        .name("§6✦ §fAccepter apprentissage §6✦")
                        .lore(
                                "§7Le joueur rejoint",
                                "§7comme apprenti.",
                                "",
                                "§8• §7Formation",
                                "§8• §7Participation",
                                "",
                                "§eClique pour accepter"
                        )
                        .action("application_accept_apprentice")
                        .target(application.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fDemander entretien §6✦")
                        .lore(
                                "§7Marque le dossier",
                                "§7comme entretien.",
                                "",
                                "§8• §7À discuter avec le joueur",
                                "",
                                "§bClique pour demander"
                        )
                        .action("application_interview")
                        .target(application.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.RED_DYE)
                        .name("§cRefuser")
                        .lore(
                                "§7Refuse cette candidature.",
                                "",
                                "§8• §7Le joueur sera prévenu",
                                "",
                                "§cClique pour refuser"
                        )
                        .action("application_refuse")
                        .target(application.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Candidatures reçues"
                        )
                        .action("application_received_list")
                        .target(application.getBusinessId())
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

        return clean.substring(
                0,
                Math.max(1, max - 3)
        ) + "...";
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