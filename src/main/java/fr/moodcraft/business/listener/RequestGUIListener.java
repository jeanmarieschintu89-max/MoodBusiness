package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.RequestCategoryGUI;
import fr.moodcraft.business.gui.RequestDetailGUI;
import fr.moodcraft.business.gui.RequestListGUI;
import fr.moodcraft.business.gui.RequestMainGUI;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.ContractManager;
import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.RequestCategory;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ContractBookUtil;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class RequestGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        String title = e.getView().getTitle();

        if (!isRequestTitle(title)) {
            return;
        }

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }

        ItemStack item = e.getCurrentItem();
        String action = ItemBuilder.getAction(item);
        String target = ItemBuilder.getTarget(item);

        if (action == null) {
            return;
        }

        switch (action) {

            case "open_requests" -> RequestMainGUI.open(p);
            case "request_create" -> RequestCategoryGUI.open(p);

            case "request_start" -> {
                RequestCategory category;
                try {
                    category = RequestCategory.valueOf(target);
                } catch (Exception ex) {
                    category = RequestCategory.AUTRE;
                }
                RequestChatListener.startRequest(p, category);
            }

            case "request_my_list" -> RequestListGUI.openMy(p);
            case "request_public_list" -> RequestListGUI.openPublic(p);

            case "request_detail" -> {
                BusinessRequest request = RequestManager.get(target);

                if (request == null) {
                    BusinessMessages.deny(p, "Demandes", "Demande introuvable.");
                    return;
                }

                RequestDetailGUI.open(p, request);
            }

            case "request_take" -> {
                BusinessRequest request = RequestManager.get(target);

                if (request == null) {
                    BusinessMessages.deny(p, "Demandes", "Demande introuvable.");
                    return;
                }

                Business business = BusinessManager.getMemberBusiness(p.getUniqueId());

                if (business == null) {
                    BusinessMessages.deny(p, "Bureau des Entreprises", "Vous n'êtes dans aucune entreprise.");
                    return;
                }

                ContractManager.ContractResult result = ContractManager.createFromRequest(p, business, request);
                p.closeInventory();

                if (!result.success()) {
                    BusinessMessages.deny(p, "Prise en charge", result.message());
                    return;
                }

                ContractBookUtil.giveProofBooks(result.contract());

                BusinessMessages.success(
                        p,
                        "Prise en charge",
                        result.message(),
                        "§8• §7Deux livres de preuve ont été générés."
                );
            }

            case "request_cancel" -> {
                BusinessRequest request = RequestManager.get(target);

                if (request == null) {
                    BusinessMessages.deny(p, "Demandes", "Demande introuvable.");
                    return;
                }

                RequestManager.RequestResult result = RequestManager.cancel(p, request);

                if (!result.success()) {
                    BusinessMessages.deny(p, "Demandes", result.message());
                    return;
                }

                BusinessMessages.success(p, "Demandes", result.message());
                RequestListGUI.openMy(p);
            }

            case "back_main" -> BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isRequestTitle(String title) {
        if (title == null) {
            return false;
        }

        String clean = cleanTitle(title);

        return clean.equals("demandes")
                || clean.equals("categorie demande")
                || clean.equals("mes demandes")
                || clean.equals("demandes publiques")
                || clean.equals("demande");
    }

    private String cleanTitle(String title) {
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
