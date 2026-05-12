package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.Alert;
import fr.moodcraft.business.model.AlertType;

import fr.moodcraft.business.storage.AlertStorage;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class AlertManager {

    private AlertManager() {}

    public static void add(
            UUID uuid,
            String playerName,
            AlertType type,
            String title,
            String message
    ) {

        if (uuid == null) {
            return;
        }

        long now =
                System.currentTimeMillis();

        String id =
                now
                        + "-"
                        + uuid.toString().substring(0, 8)
                        + "-"
                        + AlertStorage.getAll().size();

        Alert alert =
                new Alert(
                        id,
                        uuid,
                        playerName != null
                                ? playerName
                                : "Inconnu",
                        type != null
                                ? type
                                : AlertType.SYSTEM,
                        title != null
                                ? title
                                : "Notification",
                        message != null
                                ? message
                                : "",
                        now,
                        false
                );

        AlertStorage.add(alert);
    }

    public static void add(
            Player player,
            AlertType type,
            String title,
            String message
    ) {

        if (player == null) {
            return;
        }

        add(
                player.getUniqueId(),
                player.getName(),
                type,
                title,
                message
        );
    }

    public static void add(
            OfflinePlayer player,
            AlertType type,
            String title,
            String message
    ) {

        if (player == null) {
            return;
        }

        add(
                player.getUniqueId(),
                player.getName(),
                type,
                title,
                message
        );
    }

    public static int unreadCount(
            Player player
    ) {

        if (player == null) {
            return 0;
        }

        return AlertStorage.getUnread(
                player.getUniqueId()
        ).size();
    }

    public static void sendPendingAlerts(
            Player player
    ) {

        if (player == null) {
            return;
        }

        List<Alert> unread =
                AlertStorage.getUnread(
                        player.getUniqueId()
                );

        if (unread.isEmpty()) {
            return;
        }

        BusinessMessages.header(
                player,
                "Alertes " + BusinessMessages.brand()
        );

        player.sendMessage("§fVous avez des notifications économiques.");
        player.sendMessage("§7Alertes en attente: §e" + unread.size());
        player.sendMessage("");

        int shown = 0;

        for (Alert alert : unread) {

            if (shown >= 5) {
                break;
            }

            player.sendMessage(
                    "§8• "
                            + alert.getType().getDisplayName()
                            + " §8» §f"
                            + alert.getTitle()
            );

            if (alert.getMessage() != null
                    && !alert.getMessage().isBlank()) {

                player.sendMessage(
                        "§7  "
                                + crop(alert.getMessage(), 80)
                );
            }

            alert.setRead(true);
            shown++;
        }

        if (unread.size() > shown) {

            player.sendMessage("");
            player.sendMessage(
                    "§7Et §e"
                            + (unread.size() - shown)
                            + " §7autre(s) notification(s)."
            );
        }

        player.sendMessage("");
        player.sendMessage("§a✔ Consultez §e/entreprise alertes §apour l'historique.");

        BusinessMessages.footer(player);

        AlertStorage.save();
    }

    public static void showAlertHistory(
            Player player
    ) {

        if (player == null) {
            return;
        }

        List<Alert> alerts =
                AlertStorage.getAllFor(
                        player.getUniqueId(),
                        10
                );

        BusinessMessages.header(
                player,
                "Alertes " + BusinessMessages.brand()
        );

        if (alerts.isEmpty()) {

            player.sendMessage("§7Aucune alerte enregistrée.");

            BusinessMessages.footer(player);

            return;
        }

        for (Alert alert : alerts) {

            player.sendMessage(
                    "§8• "
                            + alert.getType().getDisplayName()
                            + " §8» §f"
                            + alert.getTitle()
            );

            player.sendMessage(
                    "§7  "
                            + crop(alert.getMessage(), 75)
            );

            player.sendMessage(
                    "§8  "
                            + TimeUtil.formatDate(
                            alert.getCreatedAt()
                    )
            );
        }

        BusinessMessages.footer(player);
    }

    private static String crop(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Aucun détail.";
        }

        if (text.length() <= max) {
            return text;
        }

        return text.substring(0, max) + "...";
    }
}