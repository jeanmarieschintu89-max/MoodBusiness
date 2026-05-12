package fr.moodcraft.business.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Business {

    private final String id;
    private final String name;

    private final UUID ownerUuid;
    private String ownerName;

    private BusinessStatus status;

    private double balance;

    private final int creationIndex;
    private final double creationFee;

    private final long createdAt;
    private long updatedAt;

    private final Map<UUID, BusinessRole> members =
            new LinkedHashMap<>();

    private final Map<UUID, String> memberNames =
            new LinkedHashMap<>();

    public Business(
            String id,
            String name,
            UUID ownerUuid,
            String ownerName,
            BusinessStatus status,
            double balance,
            int creationIndex,
            double creationFee,
            long createdAt
    ) {

        this.id = id;
        this.name = name;
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.status = status;
        this.balance = balance;
        this.creationIndex = creationIndex;
        this.creationFee = creationFee;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;

        addMember(
                ownerUuid,
                ownerName,
                BusinessRole.DIRIGEANT
        );
    }

    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public UUID getOwnerUuid() {

        return ownerUuid;
    }

    public String getOwnerName() {

        return ownerName;
    }

    public void setOwnerName(
            String ownerName
    ) {

        this.ownerName = ownerName;

        setMemberName(
                ownerUuid,
                ownerName
        );

        touch();
    }

    public BusinessStatus getStatus() {

        return status;
    }

    public void setStatus(
            BusinessStatus status
    ) {

        this.status = status;
        touch();
    }

    public double getBalance() {

        return balance;
    }

    public void setBalance(
            double balance
    ) {

        this.balance =
                Math.max(
                        0,
                        balance
                );

        touch();
    }

    public int getCreationIndex() {

        return creationIndex;
    }

    public double getCreationFee() {

        return creationFee;
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

    public Map<UUID, BusinessRole> getMembers() {

        return members;
    }

    public Map<UUID, String> getMemberNames() {

        return memberNames;
    }

    public BusinessRole getRole(
            UUID uuid
    ) {

        return members.get(uuid);
    }

    public String getMemberName(
            UUID uuid
    ) {

        return memberNames.getOrDefault(
                uuid,
                "Inconnu"
        );
    }

    public void setMemberName(
            UUID uuid,
            String name
    ) {

        memberNames.put(
                uuid,
                name != null
                        ? name
                        : "Inconnu"
        );

        touch();
    }

    public void addMember(
            UUID uuid,
            String name,
            BusinessRole role
    ) {

        members.put(
                uuid,
                role
        );

        memberNames.put(
                uuid,
                name != null
                        ? name
                        : "Inconnu"
        );

        touch();
    }

    public void setRole(
            UUID uuid,
            BusinessRole role
    ) {

        members.put(
                uuid,
                role
        );

        touch();
    }

    public void removeMember(
            UUID uuid
    ) {

        if (uuid.equals(ownerUuid)) {
            return;
        }

        members.remove(uuid);
        memberNames.remove(uuid);

        touch();
    }

    public boolean isOwner(
            UUID uuid
    ) {

        return ownerUuid.equals(uuid);
    }

    public boolean isMember(
            UUID uuid
    ) {

        return members.containsKey(uuid);
    }

    public boolean isActive() {

        return status == BusinessStatus.ACTIVE;
    }

    private void touch() {

        updatedAt =
                System.currentTimeMillis();
    }
}