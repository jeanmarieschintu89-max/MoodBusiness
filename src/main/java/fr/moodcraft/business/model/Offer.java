package fr.moodcraft.business.model;

import java.util.UUID;

public class Offer {

    private final String id;

    private final String requestId;

    private final String businessId;
    private final String businessName;

    private final UUID senderUuid;
    private String senderName;

    private double amount;
    private int dueDays;
    private String comment;

    private OfferStatus status;

    private final long createdAt;
    private long updatedAt;

    public Offer(
            String id,
            String requestId,
            String businessId,
            String businessName,
            UUID senderUuid,
            String senderName,
            double amount,
            int dueDays,
            String comment,
            OfferStatus status,
            long createdAt,
            long updatedAt
    ) {

        this.id = id;
        this.requestId = requestId;
        this.businessId = businessId;
        this.businessName = businessName;
        this.senderUuid = senderUuid;
        this.senderName = senderName;
        this.amount = amount;
        this.dueDays = dueDays;
        this.comment = comment;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {

        return id;
    }

    public String getRequestId() {

        return requestId;
    }

    public String getBusinessId() {

        return businessId;
    }

    public String getBusinessName() {

        return businessName;
    }

    public UUID getSenderUuid() {

        return senderUuid;
    }

    public String getSenderName() {

        return senderName;
    }

    public void setSenderName(
            String senderName
    ) {

        this.senderName = senderName;
        touch();
    }

    public double getAmount() {

        return amount;
    }

    public void setAmount(
            double amount
    ) {

        this.amount = Math.max(
                0,
                amount
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

    public String getComment() {

        return comment;
    }

    public void setComment(
            String comment
    ) {

        this.comment = comment;
        touch();
    }

    public OfferStatus getStatus() {

        return status;
    }

    public void setStatus(
            OfferStatus status
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

    private void touch() {

        updatedAt =
                System.currentTimeMillis();
    }
}