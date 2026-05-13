package fr.moodcraft.business.model;

import java.util.UUID;

public class ContractAssignment {

    private final String id;

    private final String contractId;
    private final String contractTitle;

    private final String businessId;
    private final String businessName;

    private final UUID memberUuid;
    private String memberName;

    private final BusinessRole memberRole;

    private final UUID assignedByUuid;
    private String assignedByName;

    private final long assignedAt;

    private boolean active;

    public ContractAssignment(
            String id,
            String contractId,
            String contractTitle,
            String businessId,
            String businessName,
            UUID memberUuid,
            String memberName,
            BusinessRole memberRole,
            UUID assignedByUuid,
            String assignedByName,
            long assignedAt,
            boolean active
    ) {

        this.id =
                id;

        this.contractId =
                contractId;

        this.contractTitle =
                contractTitle;

        this.businessId =
                businessId;

        this.businessName =
                businessName;

        this.memberUuid =
                memberUuid;

        this.memberName =
                memberName;

        this.memberRole =
                memberRole;

        this.assignedByUuid =
                assignedByUuid;

        this.assignedByName =
                assignedByName;

        this.assignedAt =
                assignedAt;

        this.active =
                active;
    }

    public String getId() {

        return id;
    }

    public String getContractId() {

        return contractId;
    }

    public String getContractTitle() {

        return contractTitle;
    }

    public String getBusinessId() {

        return businessId;
    }

    public String getBusinessName() {

        return businessName;
    }

    public UUID getMemberUuid() {

        return memberUuid;
    }

    public String getMemberName() {

        return memberName;
    }

    public void setMemberName(
            String memberName
    ) {

        this.memberName =
                memberName != null
                        ? memberName
                        : "Inconnu";
    }

    public BusinessRole getMemberRole() {

        return memberRole;
    }

    public UUID getAssignedByUuid() {

        return assignedByUuid;
    }

    public String getAssignedByName() {

        return assignedByName;
    }

    public void setAssignedByName(
            String assignedByName
    ) {

        this.assignedByName =
                assignedByName != null
                        ? assignedByName
                        : "Inconnu";
    }

    public long getAssignedAt() {

        return assignedAt;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(
            boolean active
    ) {

        this.active =
                active;
    }
}