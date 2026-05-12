package fr.moodcraft.business.util;

import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class SafeGUI {

    private SafeGUI() {}

    private static final ItemStack FILLER =
            new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                    .name(" ")
                    .build();

    public static void fill(
            Inventory inv
    ) {

        for (int i = 0; i < inv.getSize(); i++) {

            inv.setItem(
                    i,
                    FILLER
            );
        }
    }

    public static void set(
            Inventory inv,
            int slot,
            ItemStack item
    ) {

        if (slot < 0 || slot >= inv.getSize()) {
            return;
        }

        inv.setItem(
                slot,
                item
        );
    }
}