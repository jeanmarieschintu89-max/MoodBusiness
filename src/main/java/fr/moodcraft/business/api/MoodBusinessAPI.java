package fr.moodcraft.business.api;

import fr.moodcraft.business.manager.AlertManager;

import fr.moodcraft.business.model.AlertType;

import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

import java.util.UUID;

public final class MoodBusinessAPI {

    private MoodBusinessAPI() {}

    public static void sendPendingAlerts(
            Player player
    ) {

        AlertManager.sendPendingAlerts(player);
    }

    public static int getUnreadAlerts(
            Player player
    ) {

        return AlertManager.unreadCount(player);
    }

    public static void addAlert(
            UUID uuid,
            String playerName,
            AlertType type,
            String title,
            String message
    ) {

        AlertManager.add(
                uuid,
                playerName,
                type,
                title,
                message
        );
    }

    public static void addAlert(
            Player player,
            AlertType type,
            String title,
            String message
    ) {

        AlertManager.add(
                player,
                type,
                title,
                message
        );
    }

    public static void addAlert(
            OfflinePlayer player,
            AlertType type,
            String title,
            String message
    ) {

        AlertManager.add(
                player,
                type,
                title,
                message
        );
    }
}