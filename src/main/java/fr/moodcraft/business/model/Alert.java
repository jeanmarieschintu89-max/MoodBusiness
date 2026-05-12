package fr.moodcraft.business.model;

import java.util.UUID;

public class Alert {

    private final String id;

    private final UUID playerUuid;
    private String playerName;

    private final AlertType type;

    private final String title;
    private final String message;

    private final long createdAt;

    private boolean read;

    public Alert(
            String id,
            UUID playerUuid,
            String playerName,
            AlertType type,
            String title,
            String message,
            long createdAt,
            boolean read
    ) {

        this.id = id;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public String getId() {

        return id;
    }

    public UUID getPlayerUuid() {

        return playerUuid;
    }

    public String getPlayerName() {

        return playerName;
    }

    public void setPlayerName(
            String playerName
    ) {

        this.playerName =
                playerName != null
                        ? playerName
                        : "Inconnu";
    }

    public AlertType getType() {

        return type;
    }

    public String getTitle() {

        return title;
    }

    public String getMessage() {

        return message;
    }

    public long getCreatedAt() {

        return createdAt;
    }

    public boolean isRead() {

        return read;
    }

    public void setRead(
            boolean read
    ) {

        this.read = read;
    }
}