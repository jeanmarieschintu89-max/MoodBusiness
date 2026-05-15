package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class BusinessDissolveConfirmGUI {

    public static final String TITLE = GuiTitle.of("Fermer Entreprise");

    private BusinessDissolveConfirmGUI() {}

    public static void open(Player p, Business business) {

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        SafeGUI.fill(inv);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.BARRIER)
                .name("§c✦ §fFermer l’entreprise §c✦")
                .lore(
                        "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                        "§8• §7Dirigeant : §e" + shortText(business.getOwnerName(), 18),
                        "§8• §7Banque : §e" + VaultHook.format(business.getBalance()),
                        "",
                        "§c✖ §fAction sensible",
                        "§8• §7L’entreprise sera archivée",
                        "§8• §7Banque vide requise",
                        "§8• §7Aucun contrat ouvert",
                        "§8• §7Historique gardé"
                )
                .build());

        SafeGUI.set(inv, 11, new ItemBuilder(Material.REDSTONE_BLOCK)
                .name("§c✦ §fAnnuler §c✦")
                .lore(
                        "§8• §7Ne rien changer",
                        "§8• §7Retour à la gestion",
                        "",
                        "§c✖ §fAnnuler"
                )
                .action("dissolve_cancel")
                .target(business.getId())
                .build());

        SafeGUI.set(inv, 15, new ItemBuilder(Material.LAVA_BUCKET)
                .name("§c✦ §fConfirmer §c✦")
                .lore(
                        "§8• §7Ferme l’entreprise",
                        "§8• §7Elle ne sera plus active",
                        "§8• §7Action enregistrée",
                        "§8• §7Logs conservés",
                        "",
                        "§c✖ §fFermer"
                )
                .action("dissolve_confirm")
                .target(business.getId())
                .build());

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
