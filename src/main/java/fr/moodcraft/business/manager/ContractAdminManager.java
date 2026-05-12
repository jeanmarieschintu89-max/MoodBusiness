package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;
import fr.moodcraft.business.model.TransactionType;

import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.ContractStorage;
import fr.moodcraft.business.storage.FinanceStorage;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

public final class ContractAdminManager {

    private ContractAdminManager() {}

    public static AdminResult payBusiness(
            Player staff,
            Contract contract
    ) {

        if (staff == null || contract == null) {

            return AdminResult.fail(
                    "Contrat invalide."
            );
        }

        if (!staff.hasPermission("moodbusiness.staff.litige")) {

            return AdminResult.fail(
                    "Accès réservé à l'administration économique."
            );
        }

        if (contract.getStatus() != ContractStatus.LITIGE) {

            return AdminResult.fail(
                    "Ce contrat n'est pas en litige."
            );
        }

        if (contract.getEscrowAmount() <= 0) {

            return AdminResult.fail(
                    "Aucun fonds bloqué sur ce contrat."
            );
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        if (business == null) {

            return AdminResult.fail(
                    "Entreprise introuvable."
            );
        }

        business.setBalance(
                business.getBalance()
                        + contract.getNetAmount()
        );

        contract.setEscrowAmount(0);
        contract.setStatus(
                ContractStatus.VALIDE
        );

        contract.addHistory(
                "§8• §6Décision staff §7par §f"
                        + staff.getName()
                        + " §8• §7Paiement accordé à l'entreprise."
        );

        contract.addHistory(
                "§8• §cTaxe économique §7prélevée: §c"
                        + VaultHook.format(contract.getTaxAmount())
                        + " §8("
                        + contract.getTaxRate()
                        + "%§8)"
        );

        BusinessStorage.save();
        ContractStorage.save();

        FinanceStorage.add(
                business,
                TransactionType.CONTRAT_VERSEMENT,
                contract.getNetAmount(),
                staff.getName(),
                business.getName(),
                "Décision staff litige - paiement entreprise du contrat "
                        + contract.getId()
        );

        FinanceStorage.add(
                business,
                TransactionType.TAXE,
                contract.getTaxAmount(),
                "Administration économique",
                business.getName(),
                "Taxe économique sur litige résolu "
                        + contract.getId()
        );

        AuditLogManager.log(
                AuditLogType.STAFF_ACTION,
                staff,
                contract.getTitle(),
                business,
                "Litige résolu: paiement entreprise. Net: "
                        + VaultHook.format(contract.getNetAmount())
                        + ", taxe: "
                        + VaultHook.format(contract.getTaxAmount())
        );

        AlertManager.add(
                business.getOwnerUuid(),
                business.getOwnerName(),
                AlertType.LITIGE,
                "Litige résolu",
                "Le staff a validé le paiement du contrat "
                        + contract.getTitle()
                        + ". Net reçu: "
                        + VaultHook.format(contract.getNetAmount())
                        + "."
        );

        OfflinePlayer client =
                Bukkit.getOfflinePlayer(
                        contract.getClientUuid()
                );

        AlertManager.add(
                client,
                AlertType.LITIGE,
                "Litige résolu",
                "Le staff a accordé le paiement à l'entreprise pour le contrat "
                        + contract.getTitle()
                        + "."
        );

        return AdminResult.success(
                "Litige résolu. Paiement versé à l'entreprise."
        );
    }

    public static AdminResult refundClient(
            Player staff,
            Contract contract
    ) {

        if (staff == null || contract == null) {

            return AdminResult.fail(
                    "Contrat invalide."
            );
        }

        if (!staff.hasPermission("moodbusiness.staff.litige")) {

            return AdminResult.fail(
                    "Accès réservé à l'administration économique."
            );
        }

        if (contract.getStatus() != ContractStatus.LITIGE) {

            return AdminResult.fail(
                    "Ce contrat n'est pas en litige."
            );
        }

        double refund =
                contract.getEscrowAmount();

        if (refund <= 0) {

            return AdminResult.fail(
                    "Aucun fonds bloqué sur ce contrat."
            );
        }

        OfflinePlayer client =
                Bukkit.getOfflinePlayer(
                        contract.getClientUuid()
                );

        if (!VaultHook.deposit(
                client,
                refund
        )) {

            return AdminResult.fail(
                    "Le remboursement du client a échoué."
            );
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        contract.setEscrowAmount(0);
        contract.setStatus(
                ContractStatus.ANNULE
        );

        contract.addHistory(
                "§8• §6Décision staff §7par §f"
                        + staff.getName()
                        + " §8• §7Client remboursé: §e"
                        + VaultHook.format(refund)
        );

        ContractStorage.save();

        AuditLogManager.log(
                AuditLogType.STAFF_ACTION,
                staff,
                contract.getTitle(),
                business,
                "Litige résolu: client remboursé. Montant: "
                        + VaultHook.format(refund)
        );

        AlertManager.add(
                client,
                AlertType.LITIGE,
                "Litige résolu",
                "Le staff vous a remboursé "
                        + VaultHook.format(refund)
                        + " pour le contrat "
                        + contract.getTitle()
                        + "."
        );

        if (business != null) {

            AlertManager.add(
                    business.getOwnerUuid(),
                    business.getOwnerName(),
                    AlertType.LITIGE,
                    "Litige résolu",
                    "Le staff a remboursé le client pour le contrat "
                            + contract.getTitle()
                            + "."
            );
        }

        return AdminResult.success(
                "Litige résolu. Client remboursé."
        );
    }

    public record AdminResult(
            boolean success,
            String message
    ) {

        public static AdminResult success(
                String message
        ) {

            return new AdminResult(
                    true,
                    message
            );
        }

        public static AdminResult fail(
                String message
        ) {

            return new AdminResult(
                    false,
                    message
            );
        }
    }
}