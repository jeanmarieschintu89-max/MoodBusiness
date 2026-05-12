package fr.moodcraft.business.model;

import java.util.UUID;

public class BusinessRequest {

    private final String id;

    private final UUID creatorUuid;
    private String creatorName;

    private String title;
    private String description;

    private double budget;
    private int dueDays;

    private final RequestCategory category;
    private RequestStatus status;

    private final long createdAt;
    private long updatedAt;
    private final long expiresAt;

    private String acceptedOfferId;

    public BusinessRequest(
            String id,
            UUID creatorUuid,
            String creatorName,
            String title,
            String description,
            double budget,
            int dueDays,
            RequestCategory category,
            RequestStatus status,
            long createdAt,
            long updatedAt,
            long expiresAt,
            String acceptedOfferId
    ) {

        this.id = id;
        this.creatorUuid = creatorUuid;
        this.creatorName = creatorName;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.dueDays = dueDays;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.acceptedOfferId = acceptedOfferId;
    }

    public String getId() {

        return id;
    }

    public UUID getCreatorUuid() {

        return creatorUuid;
    }

    public String getCreatorName() {

        return creatorName;
    }

    public void setCreatorName(
            String creatorName
    ) {

        this.creatorName = creatorName;
        touch();
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(
            String title
    ) {

        this.title = title;
        touch();
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(
            String description
    ) {

        this.description = description;
        touch();
    }

    public double getBudget() {

        return budget;
    }

    public void setBudget(
            double budget
    ) {

        this.budget = Math.max(
                0,
                budget
        );

        touch();
    }

    public int getDueDays() {

        return dueDays;
    }

    public void setDueDays(
            int dueDays
    ) {

        this.dueDays = Math.max(
                1,
                dueDays
        );

        touch();
    }

    public RequestCategory getCategory() {

        return category;
    }

    public RequestStatus getStatus() {

        return status;
    }

    public void setStatus(
            RequestStatus status
    ) {

        this.status = status;
        touch();
    }

    public long getCreatedAt() {

        return createdAt;
    }

    public long getUpdatedAt() {

        return updatedAt;
    }

    public void setUpdatedAt(
            long updatedAt
    ) {

        this.updatedAt = updatedAt;
    }

    public long getExpiresAt() {

        return expiresAt;
    }

    public String getAcceptedOfferId() {

        return acceptedOfferId;
    }

    public void setAcceptedOfferId(
            String acceptedOfferId
    ) {

        this.acceptedOfferId = acceptedOfferId;
        touch();
    }

    public boolean isExpired() {

        return expiresAt > 0
                && System.currentTimeMillis() > expiresAt;
    }

    private void touch() {

        updatedAt =
                System.currentTimeMillis();
    }
}