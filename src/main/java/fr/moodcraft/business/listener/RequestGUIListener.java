package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.OfferListGUI;
import fr.moodcraft.business.gui.RequestCategoryGUI;
import fr.moodcraft.business.gui.RequestDetailGUI;
import fr.moodcraft.business.gui.RequestListGUI;
import fr.moodcraft.business.gui.RequestMainGUI;

import fr.moodcraft.business.manager.OfferManager;
import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.Offer;
import fr.moodcraft.business.model.RequestCategory;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class RequestGUIListener implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!isRequestTitle(title)) {
            return;
        }

        e.setCancelled(true);

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

        switch (action) {

            case "open_requests" ->
                    RequestMainGUI.open(p);

            case "request_create" ->
                    RequestCategoryGUI.open(p);

            case "request_start" -> {

                RequestCategory category;

                try {

                    category =
                            RequestCategory.valueOf(target);

                } catch (Exception ex) {

                    category =
                            RequestCategory.AUTRE;
                }

                RequestChatListener.startRequest(
                        p,
                        category
                );
            }

            case "request_my_list" ->
                    RequestListGUI.openMy(p);

            case "request_public_list" ->
                    RequestListGUI.openPublic(p);

            case "request_detail" -> {

                BusinessRequest request =
                        RequestManager.get(target);

                if (request == null) {

                    BusinessMessages.deny(
                            p,
                            "Demandes",
                            "Demande introuvable."
                    );

                    return;
                }

                RequestDetailGUI.open(
                        p,
                        request
                );
            }

            case "request_cancel" -> {

                BusinessRequest request =
                        RequestManager.get(target);

                if (request == null) {

                    BusinessMessages.deny(
                            p,
                            "Demandes",
                            "Demande introuvable."
                    );

                    return;
                }

                RequestManager.RequestResult result =
                        RequestManager.cancel(
                                p,
                                request
                        );

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Demandes",
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Demandes",
                        result.message()
                );

                RequestListGUI.openMy(p);
            }

            case "offer_start" -> {

                BusinessRequest request =
                        RequestManager.get(target);

                if (request == null) {

                    BusinessMessages.deny(
                            p,
                            "Offre Entreprise",
                            "Demande introuvable."
                    );

                    return;
                }

                RequestChatListener.startOffer(
                        p,
                        request
                );
            }

            case "offer_list" -> {

                BusinessRequest request =
                        RequestManager.get(target);

                if (request == null) {

                    BusinessMessages.deny(
                            p,
                            "Offres Reçues",
                            "Demande introuvable."
                    );

                    return;
                }

                if (!RequestManager.canManageRequest(
                        p,
                        request
                )) {

                    BusinessMessages.deny(
                            p,
                            "Offres Reçues",
                            "Vous ne pouvez pas consulter ces offres."
                    );

                    return;
                }

                OfferListGUI.open(
                        p,
                        request
                );
            }

            case "offer_accept" -> {

                Offer offer =
                        OfferManager.get(target);

                if (offer == null) {

                    BusinessMessages.deny(
                            p,
                            "Offres Reçues",
                            "Offre introuvable."
                    );

                    return;
                }

                BusinessRequest request =
                        RequestManager.get(
                                offer.getRequestId()
                        );

                if (request == null) {

                    BusinessMessages.deny(
                            p,
                            "Offres Reçues",
                            "Demande introuvable."
                    );

                    return;
                }

                OfferManager.OfferResult result =
                        OfferManager.acceptOffer(
                                p,
                                request,
                                offer
                        );

                p.closeInventory();

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Offres Reçues",
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Offres Reçues",
                        result.message()
                );
            }

            case "back_main" ->
                    BusinessMainGUI.open(p);

            default -> {}
        }
    }

    private boolean isRequestTitle(
            String title
    ) {

        return title.equals(RequestMainGUI.TITLE)
                || title.equals(RequestCategoryGUI.TITLE)
                || title.equals(RequestListGUI.TITLE_PUBLIC)
                || title.equals(RequestListGUI.TITLE_MY)
                || title.equals(RequestDetailGUI.TITLE)
                || title.equals(OfferListGUI.TITLE);
    }
}
