package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.ContractAdminResolveGUI;
import fr.moodcraft.business.gui.ContractDetailGUI;
import fr.moodcraft.business.gui.ContractListGUI;
import fr.moodcraft.business.gui.ContractMainGUI;

import fr.moodcraft.business.manager.ContractAdminManager;
import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ContractGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        String title = e.getView().getTitle();

        if (!isContractTitle(title) && !isMainTitle(title)) {
            return;
        }

        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }

        ItemStack item = e.getCurrentItem();
        String action = ItemBuilder.getAction(item);
        String target = ItemBuilder.getTarget(item);

        if (action == null || !isContractAction(action)) {
            return;
        }

        e.setCancelled(true);

        switch (action) {

            case "open_contracts" -> ContractMainGUI.open(p);

            case "contract_my_list" -> ContractListGUI.openMy(p);

            case "contract_business_list" -> ContractListGUI.openBusiness(p, target);

            case "contract_litige_list" -> {
                if (!p.hasPermission("moodbusiness.staff.litige")) {
                    BusinessMessages.deny(p, "Litiges Économiques", "Accès réservé à l'administration économique.");
                    return;
                }
                ContractListGUI.openLitiges(p);
            }

            case "contract_detail" -> {
                Contract contract = ContractManager.get(target);

                if (contract == null) {
                    BusinessMessages.deny(p, "Contrat Officiel", "Contrat introuvable.");
                    return;
                }

                if (!ContractManager.canView(p, contract) && !p.hasPermission("moodbusiness.staff.litige")) {
                    BusinessMessages.deny(p, "Contrat Officiel", "Vous ne pouvez pas consulter ce contrat.");
                    return;
                }

                ContractDetailGUI.open(p, contract);
            }

            case "contract_complete_chat" -> {
                Contract contract = ContractManager.get(target);

                if (contract == null) {
                    BusinessMessages.deny(p, "Contrat Officiel", "Contrat introuvable.");
                    return;
                }

                ContractChatListener.startComplete(p, contract);
            }

            case "contract_litige_chat" -> {
                Contract contract = ContractManager.get(target);

                if (contract == null) {
                    BusinessMessages.deny(p, "Litige Économique", "Contrat introuvable.");
                    return;
                }

                ContractChatListener.startLitige(p, contract);
            }

            case "contract_validate" -> {
                Contract contract = ContractManager.get(target);

                if (contract == null) {
                    BusinessMessages.deny(p, "Contrat Officiel", "Contrat introuvable.");
                    return;
                }

                ContractManager.ContractResult result = ContractManager.validate(p, contract);
                p.closeInventory();

                if (!result.success()) {
                    BusinessMessages.deny(p, "Contrat Officiel", result.message());
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.85f);
                    return;
                }

                BusinessMessages.success(p, "Contrat Officiel", result.message());
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.1f);
            }

            case "contract_admin_resolve" -> {
                if (!p.hasPermission("moodbusiness.staff.litige")) {
                    BusinessMessages.deny(p, "Décision Litige", "Accès réservé à l'administration économique.");
                    return;
                }

                Contract contract = ContractManager.get(target);

                if (contract == null) {
                    BusinessMessages.deny(p, "Décision Litige", "Contrat introuvable.");
                    return;
                }

                ContractAdminResolveGUI.open(p, contract);
            }

            case "contract_admin_pay_business" -> handleStaffResolve(p, target, true);

            case "contract_admin_refund_client" -> handleStaffResolve(p, target, false);

            case "back_main" -> BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private void handleStaffResolve(Player p, String target, boolean payBusiness) {
        if (!p.hasPermission("moodbusiness.staff.litige")) {
            BusinessMessages.deny(p, "Décision Litige", "Accès réservé à l'administration économique.");
            return;
        }

        Contract contract = ContractManager.get(target);

        if (contract == null) {
            BusinessMessages.deny(p, "Décision Litige", "Contrat introuvable.");
            return;
        }

        ContractAdminManager.AdminResult result = payBusiness
                ? ContractAdminManager.payBusiness(p, contract)
                : ContractAdminManager.refundClient(p, contract);

        p.closeInventory();

        if (!result.success()) {
            BusinessMessages.deny(p, "Décision Litige", result.message());
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.85f);
            return;
        }

        BusinessMessages.success(p, "Décision Litige", result.message());
        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.1f);
    }

    private boolean isMainTitle(String title) {
        return cleanTitle(title).equals("bureau des entreprises");
    }

    private boolean isContractTitle(String title) {
        String clean = cleanTitle(title);

        return clean.equals("contrats")
                || clean.equals("mes contrats")
                || clean.equals("contrats entreprise")
                || clean.equals("litiges")
                || clean.equals("contrat")
                || clean.equals("decision litige");
    }

    private boolean isContractAction(String action) {
        return action.equals("open_contracts")
                || action.equals("contract_my_list")
                || action.equals("contract_business_list")
                || action.equals("contract_litige_list")
                || action.equals("contract_detail")
                || action.equals("contract_complete_chat")
                || action.equals("contract_litige_chat")
                || action.equals("contract_validate")
                || action.equals("contract_admin_resolve")
                || action.equals("contract_admin_pay_business")
                || action.equals("contract_admin_refund_client")
                || action.equals("back_main");
    }

    private String cleanTitle(String title) {
        if (title == null) {
            return "";
        }

        return title
                .replaceAll("§.", "")
                .replace("✦", "")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("ù", "u")
                .replace("ç", "c")
                .trim()
                .toLowerCase();
    }
}
