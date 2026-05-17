package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class RequestMainGUI {

    public static final String TITLE = BusinessMessages.guiTitle("Missions");

    private RequestMainGUI() {}

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        int active = RequestManager.countActiveByPlayer(p.getUniqueId());

        SafeGUI.set(inv, 4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §fMissions MoodCraft §6✦")
                        .lore(
                                "§8• §7Un joueur publie une mission.",
                                "§8• §7Une entreprise la prend en charge.",
                                "§8• §7L'argent est sécurisé au contrat.",
                                "",
                                "§e➜ §fSimple : publier ou prendre une mission"
                        )
                        .build()
        );

        SafeGUI.set(inv, 20,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fPublier une mission §6✦")
                        .lore(
                                "§8• §7Titre",
                                "§8• §7Description",
                                "§8• §7Prix",
                                "§8• §7Délai",
                                "",
                                "§e➜ §fDemander un travail"
                        )
                        .action("request_create")
                        .build()
        );

        SafeGUI.set(inv, 22,
                new ItemBuilder(Material.CHEST)
                        .name("§6✦ §fMes missions §6✦")
                        .lore(
                                "§8• §7Missions actives : §e" + active,
                                "§8• §7Voir l'état de mes demandes",
                                "§8• §7Annuler une mission ouverte",
                                "",
                                "§e➜ §fGérer mes missions"
                        )
                        .action("request_my_list")
                        .build()
        );

        SafeGUI.set(inv, 24,
                new ItemBuilder(Material.COMPASS)
                        .name("§6✦ §fMissions disponibles §6✦")
                        .lore(
                                "§8• §7Missions publiées par les joueurs.",
                                "§8• §7Une entreprise peut les prendre.",
                                "§8• §7Contrat direct, sans offre compliquée.",
                                "",
                                "§e➜ §fVoir les missions"
                        )
                        .action("request_public_list")
                        .build()
        );

        Business business = BusinessManager.getMemberBusiness(p.getUniqueId());
        if (business != null && BusinessManager.canManageContracts(p, business)) {
            SafeGUI.set(inv, 31,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fPrendre une mission §6✦")
                            .lore(
                                    "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                                    "§8• §7Choisis une mission publique.",
                                    "§8• §7L'argent du client sera sécurisé.",
                                    "",
                                    "§a✔ §fAccès autorisé"
                            )
                            .action("request_public_list")
                            .build()
            );
        }

        SafeGUI.set(inv, 40,
                new ItemBuilder(Material.BARRIER)
                        .name("§6✦ §fRetour §6✦")
                        .lore("§8• §7Bureau des Entreprises")
                        .action("back_main")
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
}
