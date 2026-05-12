package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.ContractDetailGUI;
import fr.moodcraft.business.gui.ContractListGUI;
import fr.moodcraft.business.gui.ContractMainGUI;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class ContractGUIListener implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!isContractTitle(title)
                && !title.equals(BusinessMainGUI.TITLE)) {

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

        if (!isContractAction(action)) {
            return;
        }

        e.setCancelled(true);

        switch (action) {

            case "open_contracts" ->
                    ContractMainGUI.open(p);

            case "contract_my_list" ->
                    ContractListGUI.openMy(p);

            case "contract_business_list" ->
                    ContractListGUI.openBusiness(
                            p,
                            target
                    );

            case "contract_litige_list" -> {

                if (!p.hasPermission("moodbusiness.staff.litige")) {

                    BusinessMessages.deny(
                            p,
                            "Litiges Économiques",
                            "Accès réservé à l'administration économique."
                    );

                    return;
                }

                ContractListGUI.openLitiges(p);
            }

            case "contract_detail" -> {

                Contract contract =
                        ContractManager.get(target);

                if (contract == null) {

                    BusinessMessages.deny(
                            p,
                            "Contrat Officiel",
                            "Contrat introuvable."
                    );

                    return;
                }

                if (!ContractManager.canView(
                        p,
                        contract
                )
                        && !p.hasPermission("moodbusiness.staff.litige")) {

                    BusinessMessages.deny(
                            p,
                            "Contrat Officiel",
                            "Vous ne pouvez pas consulter ce contrat."
                    );

                    return;
                }

                ContractDetailGUI.open(
                        p,
                        contract
                );
            }

            case "contract_complete_chat" -> {

                Contract contract =
                        ContractManager.get(target);

                if (contract == null) {
                    return;
                }

                ContractChatListener.startComplete(
                        p,
                        contract
                );
            }

            case "contract_litige_chat" -> {

                Contract contract =
                        ContractManager.get(target);

                if (contract == null) {
                    return;
                }

                ContractChatListener.startLitige(
                        p,
                        contract
                );
            }

            case "contract_validate" -> {

                Contract contract =
                        ContractManager.get(target);

                if (contract == null) {
                    return;
                }

                ContractManager.ContractResult result =
                        ContractManager.validate(
                                p,
                                contract
                        );

                p.closeInventory();

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Contrat Officiel",
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Contrat Officiel",
                        result.message()
                );
            }

            case "back_main" ->
                    BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isContractTitle(
            String title
    ) {

        return title.equals(ContractMainGUI.TITLE)
                || title.equals(ContractListGUI.TITLE_MY)
                || title.equals(ContractListGUI.TITLE_BUSINESS)
                || title.equals(ContractListGUI.TITLE_LITIGE)
                || title.equals(ContractDetailGUI.TITLE);
    }

    private boolean isContractAction(
            String action
    ) {

        return action.equals("open_contracts")
                || action.equals("contract_my_list")
                || action.equals("contract_business_list")
                || action.equals("contract_litige_list")
                || action.equals("contract_detail")
                || action.equals("contract_complete_chat")
                || action.equals("contract_litige_chat")
                || action.equals("contract_validate")
                || action.equals("back_main");
    }
}