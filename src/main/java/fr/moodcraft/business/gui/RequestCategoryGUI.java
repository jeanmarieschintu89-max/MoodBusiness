package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.RequestCategory;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

public final class RequestCategoryGUI {

    public static final String TITLE =
            "§8✦ §6Catégorie Demande §8✦";

    private RequestCategoryGUI() {}

    public static void open(
            Player p
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        add(
                inv,
                10,
                RequestCategory.CONSTRUCTION,
                Material.BRICKS,
                "§7Construction de maisons, bâtiments, routes."
        );

        add(
                inv,
                12,
                RequestCategory.LIVRAISON,
                Material.MINECART,
                "§7Transport ou livraison de ressources."
        );

        add(
                inv,
                14,
                RequestCategory.AGRICULTURE,
                Material.WHEAT,
                "§7Production, récolte ou ferme."
        );

        add(
                inv,
                16,
                RequestCategory.MINAGE,
                Material.IRON_PICKAXE,
                "§7Récolte, minage ou excavation."
        );

        add(
                inv,
                20,
                RequestCategory.COMMERCE,
                Material.EMERALD,
                "§7Achat, vente ou fourniture."
        );

        add(
                inv,
                22,
                RequestCategory.IMMOBILIER,
                Material.OAK_DOOR,
                "§7Logement, terrain ou location."
        );

        add(
                inv,
                24,
                RequestCategory.EVENEMENT,
                Material.FIREWORK_ROCKET,
                "§7Event, animation ou décoration."
        );

        add(
                inv,
                30,
                RequestCategory.SERVICE,
                Material.NAME_TAG,
                "§7Service personnalisé."
        );

        add(
                inv,
                32,
                RequestCategory.AUTRE,
                Material.PAPER,
                "§7Demande spéciale."
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir au menu demandes."
                        )
                        .action("open_requests")
                        .build()
        );

        p.openInventory(inv);
    }

    private static void add(
            Inventory inv,
            int slot,
            RequestCategory category,
            Material material,
            String description
    ) {

        SafeGUI.set(
                inv,
                slot,
                new ItemBuilder(material)
                        .name("§6✦ " + category.getDisplayName() + " §6✦")
                        .lore(
                                description,
                                "",
                                "§a✔ Choisir cette catégorie"
                        )
                        .action("request_start")
                        .target(category.name())
                        .build()
        );
    }
}