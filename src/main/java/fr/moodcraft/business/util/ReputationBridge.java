package fr.moodcraft.business.util;

import org.bukkit.Bukkit;

import org.bukkit.OfflinePlayer;

public final class ReputationBridge {

    private ReputationBridge() {}

    public static void add(
            OfflinePlayer player,
            int points,
            String reason
    ) {

        if (player == null) {
            return;
        }

        if (points == 0) {
            return;
        }

        String name =
                player.getName();

        if (name == null || name.isBlank()) {
            return;
        }

        if (!Bukkit.getPluginManager()
                .isPluginEnabled("MoodCraftBridge")) {
            return;
        }

        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "reputation admin ajouter "
                        + name
                        + " "
                        + points
        );
    }
}