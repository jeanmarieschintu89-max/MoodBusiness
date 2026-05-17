package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.RequestCategory;
import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class RequestCategoryGUI {

    public static final String TITLE = GuiTitle.of("Type de mission");

    private RequestCategoryGUI() {}

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);
        SafeGUI.fill(inv);

        add(inv, 11, RequestCategory.CONSTRUCTION, Material.BRICKS, "§7Build, maison, route, déco");
        add(inv, 13, RequestCategory.RESSOURCES, Material.CHEST, "§7Minage, récolte, livraison");
        add(inv, 15, RequestCategory.SERVICE, Material.NAME_TAG, "§7Aide, commerce, événement");
        add(inv, 22, RequestCategory.AUTRE, Material.PAPER, "§7Mission spéciale");

        SafeGUI.set(inv, 40,
                new ItemBuilder(Material.BARRIER)
                        .name("§c✦ §fRetour §c✦")
                        .lore("§8• §7Bureau des Entreprises")
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }

    private static void add(Inventory inv, int slot, RequestCategory category, Material material, String description) {
        SafeGUI.set(inv, slot,
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
