package fr.moodcraft.business.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Contract {

    private final String id;

    private final String requestId;
    private final String offerId;

    private final UUID clientUuid;
    private String clientName;

    private final String businessId;
    private final String businessName;

    private final UUID businessActorUuid;
    private String businessActorName;

    private String title;
    private String description;

    private double grossAmount;
    private double taxRate;
    private double taxAmount;
    private double netAmount;
    private double escrowAmount;

    private int dueDays;

    private ContractStatus status;

    private final long createdAt;
    private long updatedAt;

    private long dueAt;
    private long completedAt;
    private long validateBefore;

    private final List<String> history =
            new ArrayList<>();

    public Contract(
            String id,
            String requestId,
            String offerId,
            UUID clientUuid,
            String clientName,
            String businessId,
            String businessName,
            UUID businessActorUuid,
            String businessActorName,
            String title,
            String description,
            double grossAmount,
            double taxRate,
            double taxAmount,
            double netAmount,
            double escrowAmount,
            int dueDays,
            ContractStatus status,
            long createdAt,
            long updatedAt,
            long dueAt,
            long completedAt,
            long validateBefore,
            List<String> history
    ) {

        this.id = id;
        this.requestId = requestId;
        this.offerId = offerId;
        this.clientUuid = clientUuid;
        this.clientName = clientName;
        this.businessId = businessId;
        this.businessName = businessName;
        this.businessActorUuid = businessActorUuid;
        this.businessActorName = businessActorName;
        this.title = title;
        this.description = description;
        this.grossAmount = grossAmount;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.netAmount = netAmount;
        this.escrowAmount = escrowAmount;
        this.dueDays = dueDays;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.dueAt = dueAt;
        this.completedAt = completedAt;
        this.validateBefore = validateBefore;

        if (history != null) {

            this.history.addAll(history);
        }
    }

    public String getId() {

        return id;
    }

    public String getRequestId() {

        return requestId;
    }

    public String getOfferId() {

        return offerId;
    }

    public UUID getClientUuid() {

        return clientUuid;
    }

    public String getClientName() {

        return clientName;
    }

    public void setClientName(
            String clientName
    ) {

        this.clientName = clientName;
        touch();
    }

    public String getBusinessId() {

        return businessId;
    }

    public String getBusinessName() {

        return businessName;
    }

    public UUID getBusinessActorUuid() {

        return businessActorUuid;
    }

    public String getBusinessActorName() {

        return businessActorName;
    }

    public void setBusinessActorName(
            String businessActorName
    ) {

        this.businessActorName = businessActorName;
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

    public double getGrossAmount() {

        return grossAmount;
    }

    public void setGrossAmount(
            double grossAmount
    ) {

        this.grossAmount = Math.max(
                0,
                grossAmount
        );

        touch();
    }

    public double getTaxRate() {

        return taxRate;
    }

    public void setTaxRate(
            double taxRate
    ) {

        this.taxRate = Math.max(
                0,
                taxRate
        );

        touch();
    }

    public double getTaxAmount() {

        return taxAmount;
    }

    public void setTaxAmount(
            double taxAmount
    ) {

        this.taxAmount = Math.max(
                0,
                taxAmount
        );

        touch();
    }

    public double getNetAmount() {

        return netAmount;
    }

    public void setNetAmount(
            double netAmount
    ) {

        this.netAmount = Math.max(
                0,
                netAmount
        );

        touch();
    }

    public double getEscrowAmount() {

        return escrowAmount;
    }

    public void setEscrowAmount(
            double escrowAmount
    ) {

        this.escrowAmount = Math.max(
                0,
                escrowAmount
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

    public ContractStatus getStatus() {

        return status;
    }

    public void setStatus(
            ContractStatus status
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

    public long getDueAt() {

        return dueAt;
    }

    public void setDueAt(
            long dueAt
    ) {

        this.dueAt = dueAt;
        touch();
    }

    public long getCompletedAt() {

        return completedAt;
    }

    public void setCompletedAt(
            long completedAt
    ) {

        this.completedAt = completedAt;
        touch();
    }

    public long getValidateBefore() {

        return validateBefore;
    }

    public void setValidateBefore(
            long validateBefore
    ) {

        this.validateBefore = validateBefore;
        touch();
    }

    public List<String> getHistory() {

        return history;
    }

    public void addHistory(
            String line
    ) {

        if (line != null && !line.isBlank()) {

            history.add(line);
            touch();
        }
    }

    private void touch() {

        updatedAt =
                System.currentTimeMillis();
    }
}