package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessMainGUI {

    public static final String TITLE =
            "§6✦ §8Bureau des Entreprises §6✦";

    private BusinessMainGUI() {}

    public static void open(
            Player p
    ) {

        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        SafeGUI.fill(inv);

        Business business = BusinessManager.getMemberBusiness(p.getUniqueId());
        double nextPrice = BusinessManager.getCreationPrice(p.getUniqueId());

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.LECTERN)
                        .name("§6✦ §fBureau des Entreprises §6✦")
                        .lore(
                                "§8• §7Entreprises",
                                "§8• §7Demandes",
                                "§8• §7Candidatures",
                                "§8• §7Contrats",
                                "",
                                "§e➜ §fService officiel de §aMood§6Craft"
                        )
                        .build()
        );

        if (business == null) {

            SafeGUI.set(
                    inv,
                    20,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fCréer une entreprise §6✦")
                            .lore(
                                    "§8• §7Création directe",
                                    "§8• §7Nom saisi dans le chat",
                                    "§8• §7Frais : §e" + VaultHook.format(nextPrice),
                                    "",
                                    "§e➜ §fCommencer"
                            )
                            .action("business_creation_chat")
                            .build()
            );

        } else {

            BusinessRole role = business.getRole(p.getUniqueId());

            SafeGUI.set(
                    inv,
                    20,
                    new ItemBuilder(Material.GOLDEN_HELMET)
                            .name("§6✦ §fMon entreprise §6✦")
                            .lore(
                                    "§8• §7Nom : §e" + shortText(business.getName(), 18),
                                    "§8• §7Rôle : " + (role != null ? role.getDisplayName() : "§7Membre"),
                                    "§8• §7État : " + business.getStatus().getDisplayName(),
                                    "",
                                    "§e➜ §fOuvrir le tableau de bord"
                            )
                            .action("open_business_dashboard")
                            .target(business.getId())
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.PAPER)
                        .name("§6✦ §fDemandes / Missions §6✦")
                        .lore(
                                "§8• §7Créer une demande",
                                "§8• §7Voir mes demandes",
                                "§8• §7Répondre aux besoins",
                                "",
                                "§e➜ §fOuvrir les demandes"
                        )
                        .action("open_requests")
                        .build()
        );

        SafeGUI.set(
                inv,
                24,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §fCandidatures / Emploi §6✦")
                        .lore(
                                "§8• §7Postuler",
                                "§8• §7Voir mes candidatures",
                                "§8• §7Traiter les demandes reçues",
                                "",
                                "§e➜ §fOuvrir les candidatures"
                        )
                        .action("open_applications")
                        .build()
        );

        SafeGUI.set(
                inv,
                31,
                new ItemBuilder(Material.COMPASS)
                        .name("§6✦ §fEntreprises publiques §6✦")
                        .lore(
                                "§8• §7Liste des entreprises actives",
                                "§8• §7Informations publiques",
                                "§8• §7Trouver une activité",
                                "",
                                "§e➜ §fConsulter"
                        )
                        .action("open_public_active")
                        .build()
        );

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.BARRIER)
                        .name("§c✦ §fRetour §c✦")
                        .lore(
                                "§8• §7Retour au menu principal",
                                "",
                                "§c✖ §fOuvrir /menu"
                        )
                        .action("back_server_menu")
                        .build()
        );

        if (p.hasPermission("moodbusiness.staff")) {

            SafeGUI.set(
                    inv,
                    49,
                    new ItemBuilder(Material.NETHER_STAR)
                            .name("§6✦ §fGestion staff §6✦")
                            .lore(
                                    "§8• §7Suspensions",
                                    "§8• §7Logs",
                                    "§8• §7Litiges",
                                    "",
                                    "§c✖ §fAccès staff"
                            )
                            .action("open_staff")
                            .build()
            );
        }

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
        }

        String clean = text.replaceAll("§.", "").trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
