package fr.moodcraft.business.model;

public class AuditLogEntry {

    private final String id;

    private final AuditLogType type;

    private final String actorName;
    private final String targetName;

    private final String businessId;
    private final String businessName;

    private final String message;

    private final long createdAt;

    public AuditLogEntry(
            String id,
            AuditLogType type,
            String actorName,
            String targetName,
            String businessId,
            String businessName,
            String message,
            long createdAt
    ) {

        this.id = id;
        this.type = type;
        this.actorName = actorName;
        this.targetName = targetName;
        this.businessId = businessId;
        this.businessName = businessName;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getId() {

        return id;
    }

    public AuditLogType getType() {

        return type;
    }

    public String getActorName() {

        return actorName;
    }

    public String getTargetName() {

        return targetName;
    }

    public String getBusinessId() {

        return businessId;
    }

    public String getBusinessName() {

        return businessName;
    }

    public String getMessage() {

        return message;
    }

    public long getCreatedAt() {

        return createdAt;
    }
}