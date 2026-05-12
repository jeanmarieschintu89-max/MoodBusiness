package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.AlertManager;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;

public class AlertJoinListener implements Listener {

    @EventHandler
    public void onJoin(
            PlayerJoinEvent e
    ) {

        boolean fallback =
                Main.getInstance()
                        .getConfig()
                        .getBoolean(
                                "alerts.send-on-join-if-no-auth-plugin",
                                false
                        );

        if (!fallback) {
            return;
        }

        boolean moodAuthLoaded =
                Bukkit.getPluginManager().isPluginEnabled(
                        "MoodAuth"
                );

        if (moodAuthLoaded) {
            return;
        }

        Player player =
                e.getPlayer();

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> AlertManager.sendPendingAlerts(player),
                40L
        );
    }
}