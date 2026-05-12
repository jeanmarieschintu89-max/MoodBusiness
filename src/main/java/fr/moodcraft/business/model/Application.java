package fr.moodcraft.business.model;

import java.util.UUID;

public class Application {

    private final String id;

    private final String businessId;
    private final String businessName;

    private final UUID applicantUuid;
    private String applicantName;

    private final ApplicationType type;
    private ApplicationStatus status;

    private String presentation;
    private String availability;

    private final long createdAt;
    private long updatedAt;
    private final long expiresAt;

    private String decisionBy;
    private String decisionReason;

    public Application(
            String id,
            String businessId,
            String businessName,
            UUID applicantUuid,
            String applicantName,
            ApplicationType type,
            ApplicationStatus status,
            String presentation,
            String availability,
            long createdAt,
            long updatedAt,
            long expiresAt,
            String decisionBy,
            String decisionReason
    ) {

        this.id = id;
        this.businessId = businessId;
        this.businessName = businessName;
        this.applicantUuid = applicantUuid;
        this.applicantName = applicantName;
        this.type = type;
        this.status = status;
        this.presentation = presentation;
        this.availability = availability;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.decisionBy = decisionBy;
        this.decisionReason = decisionReason;
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

    public UUID getApplicantUuid() {

        return applicantUuid;
    }

    public String getApplicantName() {

        return applicantName;
    }

    public void setApplicantName(
            String applicantName
    ) {

        this.applicantName = applicantName;
        touch();
    }

    public ApplicationType getType() {

        return type;
    }

    public ApplicationStatus getStatus() {

        return status;
    }

    public void setStatus(
            ApplicationStatus status
    ) {

        this.status = status;
        touch();
    }

    public String getPresentation() {

        return presentation;
    }

    public void setPresentation(
            String presentation
    ) {

        this.presentation = presentation;
        touch();
    }

    public String getAvailability() {

        return availability;
    }

    public void setAvailability(
            String availability
    ) {

        this.availability = availability;
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

    public String getDecisionBy() {

        return decisionBy;
    }

    public void setDecisionBy(
            String decisionBy
    ) {

        this.decisionBy = decisionBy;
        touch();
    }

    public String getDecisionReason() {

        return decisionReason;
    }

    public void setDecisionReason(
            String decisionReason
    ) {

        this.decisionReason = decisionReason;
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