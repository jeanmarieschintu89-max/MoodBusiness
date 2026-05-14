package fr.moodcraft.business.util;

import fr.moodcraft.business.Main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

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
                new ItemStack(material == null ? Material.BARRIER : material);
    }

    public ItemBuilder name(
            String name
    ) {

        if (isReturnButton(name)) {
            item.setType(Material.BARRIER);
        }

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

            meta.setLore(normalizeLore(lore));
            hide(meta);
            item.setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder action(String action) {
        setString(ACTION_KEY, action);
        return this;
    }

    public ItemBuilder target(String target) {
        setString(TARGET_KEY, target);
        return this;
    }

    private void setString(String key, String value) {

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(Main.getInstance(), key),
                    PersistentDataType.STRING,
                    value == null ? "" : value
            );

            item.setItemMeta(meta);
        }
    }

    public ItemStack build() {
        return item;
    }

    public static String getAction(ItemStack item) {
        return getString(item, ACTION_KEY);
    }

    public static String getTarget(ItemStack item) {
        return getString(item, TARGET_KEY);
    }

    private static String getString(ItemStack item, String key) {

        if (item == null || item.getType().isAir()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        String value = meta.getPersistentDataContainer().get(
                new NamespacedKey(Main.getInstance(), key),
                PersistentDataType.STRING
        );

        return value == null || value.isBlank() ? null : value;
    }

    private static List<String> normalizeLore(String... lore) {

        List<String> result = new ArrayList<>();

        if (lore == null) {
            return result;
        }

        for (String line : lore) {
            if (line == null || line.isBlank()) {
                result.add("");
            } else {
                result.add(normalizeLine(line));
            }
        }

        return result;
    }

    private static String normalizeLine(String line) {

        String trimmed = line.trim().replace("§c✘", "§c✖");

        if (trimmed.startsWith("§8•")
                || trimmed.startsWith("§e➜")
                || trimmed.startsWith("§a✔")
                || trimmed.startsWith("§c✖")) {
            return trimmed;
        }

        if (trimmed.startsWith("§eClique") || trimmed.startsWith("§aClique")) {
            return "§e➜ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§cClique")) {
            return "§c✖ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§cAccès")
                || trimmed.startsWith("§cRéservé")
                || trimmed.startsWith("§cNon")
                || trimmed.startsWith("§cAction")) {
            return "§c✖ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§a")) {
            return "§a✔ §f" + cleanPrefix(trimmed);
        }

        if (trimmed.startsWith("§7") || trimmed.startsWith("§8")) {
            return "§8• §7" + cleanPrefix(trimmed);
        }

        return trimmed;
    }

    private static boolean isReturnButton(String name) {
        if (name == null) {
            return false;
        }

        String clean = name
                .replaceAll("§.", "")
                .replace("✦", "")
                .trim()
                .toLowerCase();

        return clean.equals("retour")
                || clean.equals("fermer")
                || clean.equals("revenir")
                || clean.equals("annuler")
                || clean.contains("retour au menu")
                || clean.contains("fermer le menu");
    }

    private static String cleanPrefix(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replaceFirst("^§[0-9a-fk-or]", "")
                .replaceFirst("^➜\\s*", "")
                .replaceFirst("^✔\\s*", "")
                .replaceFirst("^✘\\s*", "")
                .replaceFirst("^✖\\s*", "")
                .replaceFirst("^•\\s*", "")
                .trim();
    }

    private static void hide(ItemMeta meta) {
        if (meta == null) {
            return;
        }

        meta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_ADDITIONAL_TOOLTIP
        );

        try {
            meta.addItemFlags(ItemFlag.valueOf("HIDE_ITEM_SPECIFICS"));
        } catch (IllegalArgumentException ignored) {
        }
    }
}