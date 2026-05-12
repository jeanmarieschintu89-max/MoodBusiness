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
            "§8✦ §6Dossier Candidature §8✦";

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
                        .name("§6✦ §f" + application.getApplicantName() + " §6✦")
                        .lore(
                                "§7Entreprise: §e" + application.getBusinessName(),
                                "§7Type: " + application.getType().getDisplayName(),
                                "§7Statut: " + application.getStatus().getDisplayName(),
                                "§7Créée le: §f" + TimeUtil.formatDate(application.getCreatedAt()),
                                "",
                                "§6✦ §fPrésentation",
                                "§7" + crop(application.getPresentation()),
                                "",
                                "§6✦ §fDisponibilités",
                                "§7" + crop(application.getAvailability())
                        )
                        .build()
        );

        SafeGUI.set(
                inv,
                20,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fAccepter comme stagiaire §6✦")
                        .lore(
                                "§7Le joueur rejoindra l'entreprise",
                                "§7avec un rôle très limité.",
                                "",
                                "§b✔ Stage"
                        )
                        .action("application_accept_stage")
                        .target(application.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.COPPER_INGOT)
                        .name("§6✦ §fAccepter comme apprenti §6✦")
                        .lore(
                                "§7Le joueur rejoindra l'entreprise",
                                "§7pour une formation active.",
                                "",
                                "§e✔ Apprentissage"
                        )
                        .action("application_accept_apprentice")
                        .target(application.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fDemander un entretien §6✦")
                        .lore(
                                "§7Marquer la candidature comme",
                                "§7en attente d'entretien.",
                                "",
                                "§b✔ Contact conseillé"
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
                                "§7Refuser la candidature.",
                                "§7Une raison simple sera enregistrée.",
                                "",
                                "§cAction administrative"
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
                                "§7Revenir aux candidatures reçues."
                        )
                        .action("application_received_list")
                        .target(application.getBusinessId())
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

        if (text.length() <= 45) {
            return text;
        }

        return text.substring(0, 45) + "...";
    }
}