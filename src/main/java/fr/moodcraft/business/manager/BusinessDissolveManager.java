package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.BusinessStatus;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;

import fr.moodcraft.business.storage.BusinessStorage;

import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.entity.Player;

public final class BusinessDissolveManager {

    private BusinessDissolveManager() {}

    public static DissolveResult dissolve(
            Player player,
            Business business
    ) {

        if (player == null || business == null) {

            return DissolveResult.fail(
                    "Dossier entreprise invalide."
            );
        }

        boolean staff =
                player.hasPermission("moodbusiness.staff.suspend");

        BusinessRole role =
                business.getRole(
                        player.getUniqueId()
                );

        boolean allowed =
                staff
                        || business.isOwner(player.getUniqueId())
                        || role == BusinessRole.GERANT;

        if (!allowed) {

            return DissolveResult.fail(
                    "Seul le dirigeant, le gérant ou l'administration peut dissoudre cette entreprise."
            );
        }

        if (business.getStatus() != BusinessStatus.ACTIVE
                && business.getStatus() != BusinessStatus.SUSPENDUE) {

            return DissolveResult.fail(
                    "Cette entreprise ne peut pas être dissoute."
            );
        }

        if (business.getBalance() > 0) {

            return DissolveResult.fail(
                    "La banque entreprise doit être vide avant dissolution. Solde actuel: §e"
                            + VaultHook.format(
                            business.getBalance()
                    )
            );
        }

        for (Contract contract :
                ContractManager.getByBusiness(business)) {

            if (contract.getStatus().isOpen()) {

                return DissolveResult.fail(
                        "Impossible de dissoudre: un contrat est encore ouvert."
                );
            }

            if (contract.getStatus() == ContractStatus.TERMINE) {

                return DissolveResult.fail(
                        "Impossible de dissoudre: un contrat terminé attend validation."
                );
            }
        }

        business.setStatus(
                BusinessStatus.ARCHIVEE
        );

        int cooldownHours =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "business.creation.cooldown-hours-after-dissolution",
                                24
                        );

        BusinessStorage.setCooldownUntil(
                business.getOwnerUuid(),
                System.currentTimeMillis()
                        + TimeUtil.hours(cooldownHours)
        );

        BusinessStorage.save();

        AuditLogManager.log(
                staff
                        ? AuditLogType.STAFF_ACTION
                        : AuditLogType.BUSINESS_SUSPENDED,
                player,
                business.getName(),
                business,
                staff
                        ? "Entreprise archivée par l'administration."
                        : "Entreprise dissoute par un responsable interne."
        );

        AlertManager.add(
                business.getOwnerUuid(),
                business.getOwnerName(),
                AlertType.BUSINESS,
                "Entreprise archivée",
                "L'entreprise "
                        + business.getName()
                        + " a été archivée."
        );

        return DissolveResult.success(
                "Entreprise dissoute et archivée."
        );
    }

    public record DissolveResult(
            boolean success,
            String message
    ) {

        public static DissolveResult success(
                String message
        ) {

            return new DissolveResult(
                    true,
                    message
            );
        }

        public static DissolveResult fail(
                String message
        ) {

            return new DissolveResult(
                    false,
                    message
            );
        }
    }
}