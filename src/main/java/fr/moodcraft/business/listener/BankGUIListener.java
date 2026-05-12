package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessBankGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.BusinessPayrollGUI;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class BankGUIListener implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!title.equals(BusinessMainGUI.TITLE)
                && !title.equals(BusinessBankGUI.TITLE)
                && !title.equals(BusinessPayrollGUI.TITLE)) {

            return;
        }

        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }

        ItemStack item =
                e.getCurrentItem();

        String action =
                ItemBuilder.getAction(item);

        String target =
                ItemBuilder.getTarget(item);

        if (action == null) {
            return;
        }

        if (!isBankAction(action)) {
            return;
        }

        e.setCancelled(true);

        switch (action) {

            case "open_bank" -> {

                Business business =
                        BusinessManager.getById(target);

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Banque Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                BusinessBankGUI.open(
                        p,
                        business
                );
            }

            case "bank_deposit_chat", "bank_deposit_help" -> {

                Business business =
                        BusinessManager.getById(target);

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Banque Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                BankChatListener.startDeposit(
                        p,
                        business
                );
            }

            case "bank_withdraw_chat", "bank_withdraw_help" -> {

                Business business =
                        BusinessManager.getById(target);

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Banque Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                BankChatListener.startWithdraw(
                        p,
                        business
                );
            }

            case "bank_prime_chat", "bank_prime_help" -> {

                Business business =
                        BusinessManager.getById(target);

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Paie Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                BankChatListener.startPrime(
                        p,
                        business
                );
            }

            case "open_payroll" -> {

                Business business =
                        BusinessManager.getById(target);

                if (business == null) {
                    return;
                }

                BusinessPayrollGUI.open(
                        p,
                        business
                );
            }

            case "payroll_salary_input", "payroll_salary_help" -> {

                if (target == null || !target.contains(":")) {
                    return;
                }

                String[] split =
                        target.split(":");

                if (split.length < 2) {
                    return;
                }

                Business business =
                        BusinessManager.getById(
                                split[0]
                        );

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Paie Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                BusinessRole role;

                try {

                    role =
                            BusinessRole.valueOf(
                                    split[1]
                            );

                } catch (Exception ex) {

                    BusinessMessages.deny(
                            p,
                            "Paie Entreprise",
                            "Rôle invalide."
                    );

                    return;
                }

                PayrollChatListener.start(
                        p,
                        business,
                        role
                );
            }

            case "payroll_run_help" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Paie Entreprise"
                );

                p.sendMessage("§fLancer la paie manuellement.");
                p.sendMessage("§7Commande: §e/entreprise paie");
                p.sendMessage("§7La paie automatique se fait chaque mois.");
                p.sendMessage("§cAucune dette automatique si la banque est vide.");

                BusinessMessages.footer(p);
            }

            case "back_main", "back_business_main" ->
                    BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isBankAction(
            String action
    ) {

        return action.equals("open_bank")
                || action.equals("bank_deposit_chat")
                || action.equals("bank_deposit_help")
                || action.equals("bank_withdraw_chat")
                || action.equals("bank_withdraw_help")
                || action.equals("bank_prime_chat")
                || action.equals("bank_prime_help")
                || action.equals("open_payroll")
                || action.equals("payroll_salary_input")
                || action.equals("payroll_salary_help")
                || action.equals("payroll_run_help")
                || action.equals("back_main")
                || action.equals("back_business_main");
    }
}