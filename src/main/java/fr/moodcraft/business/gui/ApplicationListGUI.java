package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.ApplicationManager;

import fr.moodcraft.business.model.Application;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.List;

public final class ApplicationListGUI {

    public static final String TITLE_MY = GuiTitle.of("Mes Candidatures");
    public static final String TITLE_RECEIVED = GuiTitle.of("Candidatures Reçues");

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private ApplicationListGUI() {}

    public static void openMy(Player p) {
        open(p, TITLE_MY, ApplicationManager.getByApplicant(p.getUniqueId()), false);
    }

    public static void openReceived(Player p, String businessId) {
        open(p, TITLE_RECEIVED, ApplicationManager.getPendingByBusiness(businessId), true);
    }

    private static void open(Player p, String title, List<Application> list, boolean manage) {

        Inventory inv = Bukkit.createInventory(null, 54, title);
        SafeGUI.fill(inv);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fCandidatures §6✦")
                        .lore(
                                "§8• §7Dossiers : §e" + list.size(),
                                "",
                                manage
                                        ? "§a✔ §fGestion autorisée"
                                        : "§8• §7Lecture personnelle",
                                "",
                                "§8• §7Stage",
                                "§8• §7Apprentissage",
                                "§8• §7Entretien"
                        )
                        .build()
        );

        int index = 0;

        for (Application application : list) {
            if (index >= SLOTS.length) break;

            Material icon = switch (application.getStatus()) {
                case EN_ATTENTE -> Material.PAPER;
                case ENTRETIEN -> Material.NAME_TAG;
                case ACCEPTEE_STAGE, ACCEPTEE_APPRENTISSAGE -> Material.LIME_DYE;
                case REFUSEE -> Material.RED_DYE;
                case ANNULEE, EXPIREE -> Material.GRAY_DYE;
            };

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(icon)
                            .name("§6✦ §f" + shortText(
                                    manage ? application.getApplicantName() : application.getBusinessName(),
                                    18
                            ) + " §6✦")
                            .lore(
                                    "§8• §7Entreprise : §e" + shortText(application.getBusinessName(), 16),
                                    "§8• §7Joueur : §e" + shortText(application.getApplicantName(), 16),
                                    "§8• §7Type : " + application.getType().getDisplayName(),
                                    "§8• §7État : " + application.getStatus().getDisplayName(),
                                    "§8• §7Créée : §f" + shortDate(application.getCreatedAt()),
                                    "",
                                    manage
                                            ? "§e➜ §fExaminer"
                                            : "§8• §7Dossier personnel"
                            )
                            .action(manage ? "application_review" : "coming_soon")
                            .target(application.getId())
                            .build()
            );

            index++;
        }

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§6✦ §fRetour §6✦")
                        .lore("§8• §7Menu candidatures")
                        .action("open_applications")
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private static String shortDate(long time) {
        String date = TimeUtil.formatDate(time);
        if (date == null || date.equalsIgnoreCase("Jamais")) return "Aucune";
        if (date.length() <= 10) return date;
        return date.substring(0, 10);
    }
}
