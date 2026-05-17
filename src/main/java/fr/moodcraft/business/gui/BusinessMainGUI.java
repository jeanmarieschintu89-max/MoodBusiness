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

    public static final String TITLE = GuiTitle.of("Bureau des Entreprises");

    private BusinessMainGUI() {}

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        SafeGUI.fill(inv);

        Business business = BusinessManager.getMemberBusiness(p.getUniqueId());
        double nextPrice = BusinessManager.getCreationPrice(p.getUniqueId());

        SafeGUI.set(inv, 4,
                new ItemBuilder(Material.LECTERN)
                        .name("§6✦ §fBureau des Entreprises §6✦")
                        .lore(
                                "§8• §7Créer une entreprise.",
                                "§8• §7Demander un service.",
                                "§8• §7Les entreprises prennent les missions.",
                                "",
                                "§e➜ §fSimple, direct, utile"
                        )
                        .build()
        );

        if (business == null) {
            SafeGUI.set(inv, 20,
                    new ItemBuilder(Material.EMERALD)
                            .name("§6✦ §fCréer mon entreprise §6✦")
                            .lore(
                                    "§8• §7Créer une entreprise officielle.",
                                    "§8• §7Nom saisi dans le chat.",
                                    "§8• §7Frais : §e" + VaultHook.format(nextPrice),
                                    "",
                                    "§e➜ §fCommencer"
                            )
                            .action("business_creation_chat")
                            .build()
            );
        } else {
            BusinessRole role = business.getRole(p.getUniqueId());
            SafeGUI.set(inv, 20,
                    new ItemBuilder(Material.GOLDEN_HELMET)
                            .name("§6✦ §fMon entreprise §6✦")
                            .lore(
                                    "§8• §7Nom : §e" + shortText(business.getName(), 18),
                                    "§8• §7Rôle : " + (role != null ? role.getDisplayName() : "§7Membre"),
                                    "§8• §7État : " + business.getStatus().getDisplayName(),
                                    "",
                                    "§e➜ §fGérer l'entreprise"
                            )
                            .action("open_business_dashboard")
                            .target(business.getId())
                            .build()
            );
        }

        SafeGUI.set(inv, 22,
                new ItemBuilder(Material.WRITABLE_BOOK)
                        .name("§6✦ §fDemander un service §6✦")
                        .lore(
                                "§8• §7Créer une demande à une entreprise.",
                                "§8• §7Construction, ressources, aide ou autre.",
                                "§8• §7Prix et délai choisis par le joueur.",
                                "§8• §7L'argent sera sécurisé au contrat.",
                                "",
                                "§e➜ §fPublier la mission"
                        )
                        .action("request_create")
                        .build()
        );

        SafeGUI.set(inv, 40,
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
            SafeGUI.set(inv, 49,
                    new ItemBuilder(Material.NETHER_STAR)
                            .name("§6✦ §fGestion staff §6✦")
                            .lore(
                                    "§8• §7Entreprises",
                                    "§8• §7Litiges",
                                    "§8• §7Logs",
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
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
