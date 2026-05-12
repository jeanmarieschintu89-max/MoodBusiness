package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.TransactionType;

import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.FinanceStorage;
import fr.moodcraft.business.storage.PayrollStorage;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import java.util.UUID;

public final class PayrollManager {

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM");

    private PayrollManager() {}

    public static void startTask() {

        Bukkit.getScheduler().runTaskTimer(
                Main.getInstance(),
                PayrollManager::runAutomaticMonthlyPayroll,
                20L * 60L,
                20L * 60L * 60L
        );
    }

    public static void runAutomaticMonthlyPayroll() {

        if (!Main.getInstance()
                .getConfig()
                .getBoolean(
                        "payroll.enabled",
                        true
                )) {

            return;
        }

        int payday =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "payroll.default-payday",
                                1
                        );

        LocalDate today =
                LocalDate.now();

        if (today.getDayOfMonth() != payday) {
            return;
        }

        for (Business business :
                BusinessStorage.getBusinesses()) {

            payBusiness(
                    business,
                    "Système",
                    false
            );
        }
    }

    public static PayrollResult payBusiness(
            Business business,
            String actorName,
            boolean force
    ) {

        if (business == null) {

            return PayrollResult.fail(
                    "Entreprise invalide."
            );
        }

        if (!business.isActive()) {

            return PayrollResult.fail(
                    "L'entreprise n'est pas active."
            );
        }

        String currentMonth =
                LocalDate.now().format(
                        MONTH_FORMAT
                );

        if (!force
                && PayrollStorage.getLastPaidMonth(
                business.getId()
        ).equals(currentMonth)) {

            return PayrollResult.fail(
                    "La paie mensuelle a déjà été versée ce mois-ci."
            );
        }

        double total =
                calculateTotalPayroll(business);

        if (total <= 0) {

            return PayrollResult.fail(
                    "Aucun salaire configuré pour cette entreprise."
            );
        }

        if (business.getBalance() < total) {

            FinanceStorage.add(
                    business,
                    TransactionType.PAIE_MENSUELLE,
                    0,
                    actorName,
                    business.getName(),
                    "Paie mensuelle bloquée: fonds insuffisants. Total requis: "
                            + VaultHook.format(total)
            );

            return PayrollResult.fail(
                    "Fonds insuffisants. Total requis: §e"
                            + VaultHook.format(total)
            );
        }

        int paid =
                0;

        for (Map.Entry<UUID, BusinessRole> entry :
                business.getMembers().entrySet()) {

            double salary =
                    PayrollStorage.getSalary(
                            business.getId(),
                            entry.getValue()
                    );

            if (salary <= 0) {
                continue;
            }

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(
                            entry.getKey()
                    );

            if (VaultHook.deposit(
                    target,
                    salary
            )) {

                paid++;
            }
        }

        business.setBalance(
                business.getBalance()
                        - total
        );

        PayrollStorage.setLastPaidMonth(
                business.getId(),
                currentMonth
        );

        BusinessStorage.save();

        FinanceStorage.add(
                business,
                TransactionType.PAIE_MENSUELLE,
                total,
                actorName,
                business.getName(),
                "Paie mensuelle versée à "
                        + paid
                        + " membre(s)"
        );

        return PayrollResult.success(
                "Paie mensuelle versée: §e"
                        + VaultHook.format(total)
                        + " §7à §e"
                        + paid
                        + " §7membre(s)."
        );
    }

    public static double calculateTotalPayroll(
            Business business
    ) {

        if (business == null) {
            return 0;
        }

        double total = 0;

        for (BusinessRole role :
                business.getMembers().values()) {

            total +=
                    PayrollStorage.getSalary(
                            business.getId(),
                            role
                    );
        }

        return total;
    }

    public static PayrollResult setSalary(
            Business business,
            BusinessRole role,
            double amount
    ) {

        if (business == null || role == null) {

            return PayrollResult.fail(
                    "Dossier de paie invalide."
            );
        }

        if (amount < 0) {

            return PayrollResult.fail(
                    "Le salaire ne peut pas être négatif."
            );
        }

        PayrollStorage.setSalary(
                business.getId(),
                role,
                amount
        );

        return PayrollResult.success(
                "Salaire mensuel défini pour "
                        + role.getDisplayName()
                        + "§7: §e"
                        + VaultHook.format(amount)
        );
    }

    public record PayrollResult(
            boolean success,
            String message
    ) {

        public static PayrollResult success(
                String message
        ) {

            return new PayrollResult(
                    true,
                    message
            );
        }

        public static PayrollResult fail(
                String message
        ) {

            return new PayrollResult(
                    false,
                    message
            );
        }
    }
}