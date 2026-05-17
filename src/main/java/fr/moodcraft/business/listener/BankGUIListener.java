package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessBankGUI;
import fr.moodcraft.business.gui.BusinessFinanceHistoryGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BankGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();

        if (!title.equals(BusinessMainGUI.TITLE)
                && !title.equals(BusinessBankGUI.TITLE)
                && !title.equals(BusinessFinanceHistoryGUI.TITLE)) {
            return;
        }

        if (!(e.getWhoClicked() instanceof Player p)) return;

        ItemStack item = e.getCurrentItem();
        String action = ItemBuilder.getAction(item);
        String target = ItemBuilder.getTarget(item);

        if (action == null || !isBankAction(action)) return;

        e.setCancelled(true);

        switch (action) {
            case "open_bank" -> {
                Business business = BusinessManager.getById(target);
                if (business == null) {
                    BusinessMessages.deny(p, "Argent Entreprise", "Entreprise introuvable.");
                    return;
                }
                BusinessBankGUI.open(p, business);
            }

            case "open_finance_history" -> {
                Business business = BusinessManager.getById(target);
                if (business == null) {
                    BusinessMessages.deny(p, "Argent Entreprise", "Entreprise introuvable.");
                    return;
                }
                BusinessFinanceHistoryGUI.open(p, business);
            }

            case "bank_deposit_chat", "bank_deposit_help" -> {
                Business business = BusinessManager.getById(target);
                if (business == null) {
                    BusinessMessages.deny(p, "Argent Entreprise", "Entreprise introuvable.");
                    return;
                }
                BankChatListener.startDeposit(p, business);
            }

            case "bank_withdraw_chat", "bank_withdraw_help" -> {
                Business business = BusinessManager.getById(target);
                if (business == null) {
                    BusinessMessages.deny(p, "Argent Entreprise", "Entreprise introuvable.");
                    return;
                }
                BankChatListener.startWithdraw(p, business);
            }

            case "bank_prime_chat", "bank_prime_help" -> {
                Business business = BusinessManager.getById(target);
                if (business == null) {
                    BusinessMessages.deny(p, "Argent Entreprise", "Entreprise introuvable.");
                    return;
                }
                BankChatListener.startPrime(p, business);
            }

            case "back_main", "back_business_main" -> BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isBankAction(String action) {
        return action.equals("open_bank")
                || action.equals("open_finance_history")
                || action.equals("bank_deposit_chat")
                || action.equals("bank_deposit_help")
                || action.equals("bank_withdraw_chat")
                || action.equals("bank_withdraw_help")
                || action.equals("bank_prime_chat")
                || action.equals("bank_prime_help")
                || action.equals("back_main")
                || action.equals("back_business_main");
    }
}
