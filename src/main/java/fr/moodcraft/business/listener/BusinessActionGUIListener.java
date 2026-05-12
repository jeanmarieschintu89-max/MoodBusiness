package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.ApplicationMainGUI;
import fr.moodcraft.business.gui.BusinessBankGUI;
import fr.moodcraft.business.gui.BusinessDashboardGUI;
import fr.moodcraft.business.gui.BusinessEmployeesGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.ContractMainGUI;
import fr.moodcraft.business.gui.RequestMainGUI;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class BusinessActionGUIListener
        implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!title.equals(BusinessMainGUI.TITLE)
                && !title.equals(BusinessDashboardGUI.TITLE)) {
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

        if (!isAction(action)) {
            return;
        }

        e.setCancelled(true);

        switch (action) {

            case "business_creation_chat" ->
                    BusinessCreationChatListener.start(p);

            case "open_business_dashboard" -> {

                Business business =
                        BusinessManager.getByName(target);

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Bureau des Entreprises",
                            "Entreprise introuvable."
                    );

                    return;
                }

                if (!business.isMember(
                        p.getUniqueId()
                )) {

                    BusinessMessages.deny(
                            p,
                            "Bureau des Entreprises",
                            "Vous n'appartenez pas à cette entreprise."
                    );

                    return;
                }

                BusinessDashboardGUI.open(
                        p,
                        business
                );
            }

            case "dashboard_employees" -> {

                Business business =
                        BusinessManager.getByName(target);

                if (business == null) {
                    return;
                }

                BusinessEmployeesGUI.open(
                        p,
                        business
                );
            }

            case "dashboard_bank" -> {

                Business business =
                        BusinessManager.getByName(target);

                if (business == null) {
                    return;
                }

                BusinessBankGUI.open(
                        p,
                        business
                );
            }

            case "dashboard_contracts" ->
                    ContractMainGUI.open(p);

            case "dashboard_applications" ->
                    ApplicationMainGUI.open(p);

            case "dashboard_requests" ->
                    RequestMainGUI.open(p);

            case "back_business_main" ->
                    BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isAction(
            String action
    ) {

        return action.equals("business_creation_chat")
                || action.equals("open_business_dashboard")
                || action.equals("dashboard_employees")
                || action.equals("dashboard_bank")
                || action.equals("dashboard_contracts")
                || action.equals("dashboard_applications")
                || action.equals("dashboard_requests")
                || action.equals("back_business_main");
    }
}