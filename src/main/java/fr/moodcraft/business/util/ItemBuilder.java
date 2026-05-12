package fr.moodcraft.business.util;

import fr.moodcraft.business.Main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ItemBuilder {

    private final ItemStack item;

    private static final String ACTION_KEY =
            "business_action";

    private static final String TARGET_KEY =
            "business_target";

    public ItemBuilder(
            Material material
    ) {

        this.item =
                new ItemStack(material);
    }

    public ItemBuilder name(
            String name
    ) {

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder lore(
            String... lore
    ) {

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setLore(
                    Arrays.asList(lore)
            );

            meta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES
            );

            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder action(
            String action
    ) {

        setString(
                ACTION_KEY,
                action
        );

        return this;
    }

    public ItemBuilder target(
            String target
    ) {

        setString(
                TARGET_KEY,
                target
        );

        return this;
    }

    private void setString(
            String key,
            String value
    ) {

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.getPersistentDataContainer().set(
                    new NamespacedKey(
                            Main.getInstance(),
                            key
                    ),
                    PersistentDataType.STRING,
                    value
            );

            item.setItemMeta(meta);
        }
    }

    public ItemStack build() {

        return item;
    }

    public static String getAction(
            ItemStack item
    ) {

        return getString(
                item,
                ACTION_KEY
        );
    }

    public static String getTarget(
            ItemStack item
    ) {

        return getString(
                item,
                TARGET_KEY
        );
    }

    private static String getString(
            ItemStack item,
            String key
    ) {

        if (item == null || item.getType().isAir()) {
            return null;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(
                new NamespacedKey(
                        Main.getInstance(),
                        key
                ),
                PersistentDataType.STRING
        );
    }
}