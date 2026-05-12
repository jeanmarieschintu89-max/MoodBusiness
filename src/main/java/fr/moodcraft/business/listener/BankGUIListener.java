package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessBankGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.BusinessPayrollGUI;

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

            case "bank_deposit_help" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Banque Entreprise"
                );

                p.sendMessage("§fDéposer des fonds.");
                p.sendMessage("§7Commande: §e/entreprise depot <montant>");
                p.sendMessage("§8Exemple: §e/entreprise depot 5000");

                BusinessMessages.footer(p);
            }

            case "bank_withdraw_help" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Banque Entreprise"
                );

                p.sendMessage("§fRetirer des fonds.");
                p.sendMessage("§7Commande: §e/entreprise retrait <montant>");
                p.sendMessage("§8Exemple: §e/entreprise retrait 5000");
                p.sendMessage("§7Accès: §eDirigeant, Gérant, Trésorier");

                BusinessMessages.footer(p);
            }

            case "bank_prime_help" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Paie Entreprise"
                );

                p.sendMessage("§fVerser une prime.");
                p.sendMessage("§7Commande: §e/entreprise prime <joueur> <montant>");
                p.sendMessage("§8Exemple: §e/entreprise prime Steven2621 2500");
                p.sendMessage("§7Pour une grosse prime:");
                p.sendMessage("§e/entreprise prime Steven2621 20000 confirmer");

                BusinessMessages.footer(p);
            }

            case "payroll_salary_help" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Paie Entreprise"
                );

                p.sendMessage("§fConfigurer un salaire mensuel.");
                p.sendMessage("§7Commande: §e/entreprise salaire <role> <montant>");
                p.sendMessage("§8Exemple: §e/entreprise salaire employe 4000");
                p.sendMessage("§7Rôles: stagiaire, apprenti, employe, responsable, tresorier, gerant");

                BusinessMessages.footer(p);
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

            case "back_main" ->
                    BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isBankAction(
            String action
    ) {

        return action.equals("open_bank")
                || action.equals("open_payroll")
                || action.equals("bank_deposit_help")
                || action.equals("bank_withdraw_help")
                || action.equals("bank_prime_help")
                || action.equals("payroll_salary_help")
                || action.equals("payroll_run_help")
                || action.equals("back_main");
    }
}