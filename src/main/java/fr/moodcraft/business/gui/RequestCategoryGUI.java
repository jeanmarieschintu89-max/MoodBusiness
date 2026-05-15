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
            GuiTitle.of("Catégorie Demande");

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

        add(inv, 10, RequestCategory.CONSTRUCTION, Material.BRICKS, "§7Build, maison, route");
        add(inv, 12, RequestCategory.LIVRAISON, Material.MINECART, "§7Transport de ressources");
        add(inv, 14, RequestCategory.AGRICULTURE, Material.WHEAT, "§7Farm, récolte, nourriture");
        add(inv, 16, RequestCategory.MINAGE, Material.IRON_PICKAXE, "§7Minage ou excavation");
        add(inv, 20, RequestCategory.COMMERCE, Material.EMERALD, "§7Achat ou vente");
        add(inv, 22, RequestCategory.IMMOBILIER, Material.OAK_DOOR, "§7Maison, terrain, location");
        add(inv, 24, RequestCategory.EVENEMENT, Material.FIREWORK_ROCKET, "§7Event ou animation");
        add(inv, 30, RequestCategory.SERVICE, Material.NAME_TAG, "§7Aide ou service");
        add(inv, 32, RequestCategory.AUTRE, Material.PAPER, "§7Demande spéciale");

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§c✦ §fRetour §c✦")
                        .lore(
                                "§8• §7Menu demandes"
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
                                "§eClique pour choisir"
                        )
                        .action("request_start")
                        .target(category.name())
                        .build()
        );
    }
}
