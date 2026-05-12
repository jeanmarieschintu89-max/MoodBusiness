package fr.moodcraft.business.model;

public class FinanceTransaction {

    private final String id;

    private final String businessId;
    private final String businessName;

    private final TransactionType type;

    private final double amount;

    private final String actorName;
    private final String targetName;

    private final String note;

    private final long createdAt;

    public FinanceTransaction(
            String id,
            String businessId,
            String businessName,
            TransactionType type,
            double amount,
            String actorName,
            String targetName,
            String note,
            long createdAt
    ) {

        this.id = id;
        this.businessId = businessId;
        this.businessName = businessName;
        this.type = type;
        this.amount = amount;
        this.actorName = actorName;
        this.targetName = targetName;
        this.note = note;
        this.createdAt = createdAt;
    }

    public String getId() {

        return id;
    }

    public String getBusinessId() {

        return businessId;
    }

    public String getBusinessName() {

        return businessName;
    }

    public TransactionType getType() {

        return type;
    }

    public double getAmount() {

        return amount;
    }

    public String getActorName() {

        return actorName;
    }

    public String getTargetName() {

        return targetName;
    }

    public String getNote() {

        return note;
    }

    public long getCreatedAt() {

        return createdAt;
    }
}