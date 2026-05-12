package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.TransactionType;

import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.FinanceStorage;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class BusinessBankManager {

    private BusinessBankManager() {}

    public static boolean canViewBank(
            Player player,
            Business business
    ) {

        return player != null
                && business != null
                && business.isMember(
                player.getUniqueId()
        );
    }

    public static boolean canManageBank(
            Player player,
            Business business
    ) {

        if (player == null || business == null) {
            return false;
        }

        BusinessRole role =
                business.getRole(
                        player.getUniqueId()
                );

        return role != null
                && role.canManageBank();
    }

    public static boolean canConfigurePayroll(
            Player player,
            Business business
    ) {

        if (player == null || business == null) {
            return false;
        }

        BusinessRole role =
                business.getRole(
                        player.getUniqueId()
                );

        return role == BusinessRole.DIRIGEANT;
    }

    public static BankResult deposit(
            Player player,
            Business business,
            double amount
    ) {

        if (player == null || business == null) {

            return BankResult.fail(
                    "Dossier bancaire invalide."
            );
        }

        if (!business.isMember(player.getUniqueId())) {

            return BankResult.fail(
                    "Vous n'appartenez pas à cette entreprise."
            );
        }

        if (amount <= 0) {

            return BankResult.fail(
                    "Le montant doit être supérieur à zéro."
            );
        }

        if (!VaultHook.has(
                player,
                amount
        )) {

            return BankResult.fail(
                    "Fonds personnels insuffisants."
            );
        }

        if (!VaultHook.withdraw(
                player,
                amount
        )) {

            return BankResult.fail(
                    "Le retrait de vos fonds personnels a échoué."
            );
        }

        business.setBalance(
                business.getBalance()
                        + amount
        );

        BusinessStorage.save();

        FinanceStorage.add(
                business,
                TransactionType.DEPOT,
                amount,
                player.getName(),
                business.getName(),
                "Dépôt manuel dans la banque entreprise"
        );

        AuditLogManager.log(
                AuditLogType.BANK_DEPOSIT,
                player,
                business.getName(),
                business,
                "Dépôt banque entreprise: "
                        + VaultHook.format(amount)
        );

        return BankResult.success(
                "Dépôt effectué: §e"
                        + VaultHook.format(amount)
        );
    }

    public static BankResult withdraw(
            Player player,
            Business business,
            double amount
    ) {

        if (!canManageBank(
                player,
                business
        )) {

            return BankResult.fail(
                    "Votre rôle ne permet pas de retirer des fonds."
            );
        }

        if (amount <= 0) {

            return BankResult.fail(
                    "Le montant doit être supérieur à zéro."
            );
        }

        if (business.getBalance() < amount) {

            return BankResult.fail(
                    "La banque entreprise ne contient pas assez de fonds."
            );
        }

        business.setBalance(
                business.getBalance()
                        - amount
        );

        if (!VaultHook.deposit(
                player,
                amount
        )) {

            business.setBalance(
                    business.getBalance()
                            + amount
            );

            return BankResult.fail(
                    "Le versement personnel a échoué."
            );
        }

        BusinessStorage.save();

        FinanceStorage.add(
                business,
                TransactionType.RETRAIT,
                amount,
                player.getName(),
                player.getName(),
                "Retrait depuis la banque entreprise"
        );

        AuditLogManager.log(
                AuditLogType.BANK_WITHDRAW,
                player,
                player.getName(),
                business,
                "Retrait banque entreprise: "
                        + VaultHook.format(amount)
        );

        return BankResult.success(
                "Retrait effectué: §e"
                        + VaultHook.format(amount)
        );
    }

    public static BankResult bonus(
            Player actor,
            Business business,
            UUID targetUuid,
            double amount,
            boolean confirmed
    ) {

        if (!canManageBank(
                actor,
                business
        )) {

            return BankResult.fail(
                    "Votre rôle ne permet pas de verser une prime."
            );
        }

        if (targetUuid == null
                || !business.isMember(targetUuid)) {

            return BankResult.fail(
                    "Ce joueur n'est pas membre de l'entreprise."
            );
        }

        if (amount <= 0) {

            return BankResult.fail(
                    "Le montant doit être supérieur à zéro."
            );
        }

        double maxWithoutConfirmation =
                Main.getInstance()
                        .getConfig()
                        .getDouble(
                                "payroll.max-bonus-without-confirmation",
                                10000
                        );

        if (amount > maxWithoutConfirmation
                && !confirmed) {

            return BankResult.fail(
                    "Prime élevée. Confirme avec §e/entreprise prime "
                            + business.getMemberName(targetUuid)
                            + " "
                            + Math.round(amount)
                            + " confirmer"
            );
        }

        if (business.getBalance() < amount) {

            return BankResult.fail(
                    "La banque entreprise ne contient pas assez de fonds."
            );
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(targetUuid);

        business.setBalance(
                business.getBalance()
                        - amount
        );

        if (!VaultHook.deposit(
                target,
                amount
        )) {

            business.setBalance(
                    business.getBalance()
                            + amount
            );

            return BankResult.fail(
                    "Le versement de la prime a échoué."
            );
        }

        BusinessStorage.save();

        FinanceStorage.add(
                business,
                TransactionType.PRIME,
                amount,
                actor.getName(),
                business.getMemberName(targetUuid),
                "Prime manuelle"
        );

        AuditLogManager.log(
                AuditLogType.BONUS_PAID,
                actor,
                business.getMemberName(targetUuid),
                business,
                "Prime versée: "
                        + VaultHook.format(amount)
        );

        return BankResult.success(
                "Prime versée à §e"
                        + business.getMemberName(targetUuid)
                        + "§7: §e"
                        + VaultHook.format(amount)
        );
    }

    public record BankResult(
            boolean success,
            String message
    ) {

        public static BankResult success(
                String message
        ) {

            return new BankResult(
                    true,
                    message
            );
        }

        public static BankResult fail(
                String message
        ) {

            return new BankResult(
                    false,
                    message
            );
        }
    }
}