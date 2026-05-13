package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractAssignment;
import fr.moodcraft.business.model.ContractStatus;

import fr.moodcraft.business.storage.ContractAssignmentStorage;
import fr.moodcraft.business.storage.ContractStorage;

import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class ContractAssignmentManager {

    private ContractAssignmentManager() {}

    public static AssignResult assign(
            Player actor,
            Contract contract,
            UUID memberUuid
    ) {

        if (actor == null || contract == null || memberUuid == null) {

            return AssignResult.fail(
                    "Mission invalide."
            );
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        if (business == null) {

            return AssignResult.fail(
                    "Entreprise introuvable."
            );
        }

        if (!BusinessManager.canManageContracts(
                actor,
                business
        )) {

            return AssignResult.fail(
                    "Votre rôle ne permet pas d'assigner une mission."
            );
        }

        if (!business.isMember(memberUuid)) {

            return AssignResult.fail(
                    "Ce joueur n'est pas dans l'entreprise."
            );
        }

        if (contract.getStatus() != ContractStatus.EN_COURS
                && contract.getStatus() != ContractStatus.EN_RETARD) {

            return AssignResult.fail(
                    "Ce contrat n'est pas en cours."
            );
        }

        if (ContractAssignmentStorage.hasActiveAssignment(
                contract.getId(),
                memberUuid
        )) {

            return AssignResult.fail(
                    "Ce joueur est déjà assigné à ce contrat."
            );
        }

        BusinessRole role =
                business.getRole(memberUuid);

        if (role == null) {

            return AssignResult.fail(
                    "Ce joueur n'a pas de rôle dans l'entreprise."
            );
        }

        long now =
                System.currentTimeMillis();

        String id =
                now
                        + "-"
                        + contract.getId()
                        + "-"
                        + memberUuid.toString().substring(0, 8);

        ContractAssignment assignment =
                new ContractAssignment(
                        id,
                        contract.getId(),
                        contract.getTitle(),
                        business.getId(),
                        business.getName(),
                        memberUuid,
                        business.getMemberName(memberUuid),
                        role,
                        actor.getUniqueId(),
                        actor.getName(),
                        now,
                        true
                );

        ContractAssignmentStorage.add(
                assignment
        );

        contract.addHistory(
                "§8• §bMission §7assignée à §f"
                        + business.getMemberName(memberUuid)
                        + " §8• §7Rôle: "
                        + role.getDisplayName()
        );

        ContractStorage.save();

        AuditLogManager.log(
                AuditLogType.STAFF_ACTION,
                actor,
                business.getMemberName(memberUuid),
                business,
                "Membre assigné au contrat: "
                        + contract.getTitle()
        );

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(memberUuid);

        AlertManager.add(
                target,
                AlertType.CONTRACT,
                "Nouvelle mission",
                "Vous avez été assigné au contrat "
                        + contract.getTitle()
                        + " pour "
                        + business.getName()
                        + "."
        );

        if (target.isOnline()
                && target.getPlayer() != null) {

            Player online =
                    target.getPlayer();

            BusinessMessagesHeader.sendMissionAssigned(
                    online,
                    business.getName(),
                    contract.getTitle(),
                    role.getDisplayName()
            );
        }

        return AssignResult.success(
                "Membre assigné au contrat."
        );
    }

    public static List<ContractAssignment> getByContract(
            String contractId
    ) {

        return ContractAssignmentStorage.getByContract(contractId);
    }

    public static List<ContractAssignment> getByMember(
            UUID uuid
    ) {

        return ContractAssignmentStorage.getByMember(uuid);
    }

    public record AssignResult(
            boolean success,
            String message
    ) {

        public static AssignResult success(
                String message
        ) {

            return new AssignResult(
                    true,
                    message
            );
        }

        public static AssignResult fail(
                String message
        ) {

            return new AssignResult(
                    false,
                    message
            );
        }
    }

    //
    // Petit helper interne pour éviter de toucher BusinessMessages si besoin.
    //

    private static final class BusinessMessagesHeader {

        private static void sendMissionAssigned(
                Player p,
                String businessName,
                String contractTitle,
                String role
        ) {

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§a✔ §fNouvelle mission.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + businessName);
            p.sendMessage("§7Contrat: §e" + contractTitle);
            p.sendMessage("§7Rôle: " + role);
            p.sendMessage("");
            p.sendMessage("§8• §7Ouvrez §e/contrat §7pour suivre la mission");
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");
        }
    }
}