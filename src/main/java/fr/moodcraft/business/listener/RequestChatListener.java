package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.OfferManager;
import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.RequestCategory;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestChatListener implements Listener {

    private static final Map<UUID, RequestDraft> requestDrafts =
            new HashMap<>();

    private static final Map<UUID, OfferDraft> offerDrafts =
            new HashMap<>();

    public static void startRequest(
            Player player,
            RequestCategory category
    ) {

        requestDrafts.put(
                player.getUniqueId(),
                new RequestDraft(category)
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                "Demandes " + BusinessMessages.brand()
        );

        player.sendMessage("§fÉcris le titre de ta demande.");
        player.sendMessage("§7Exemple: Maison médiévale à Utopia");
        player.sendMessage("§7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(player);
    }

    public static void startOffer(
            Player player,
            BusinessRequest request
    ) {

        offerDrafts.put(
                player.getUniqueId(),
                new OfferDraft(request.getId())
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                "Offre Entreprise"
        );

        player.sendMessage("§fIndique le montant de votre offre.");
        player.sendMessage("§7Budget demandé: §e" + VaultHook.format(request.getBudget()));
        player.sendMessage("§7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(player);
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player player =
                e.getPlayer();

        RequestDraft requestDraft =
                requestDrafts.get(
                        player.getUniqueId()
                );

        OfferDraft offerDraft =
                offerDrafts.get(
                        player.getUniqueId()
                );

        if (requestDraft == null && offerDraft == null) {
            return;
        }

        e.setCancelled(true);

        String message =
                e.getMessage();

        Bukkit.getScheduler().runTask(
                Main.getInstance(),
                () -> {

                    if (requestDraft != null) {

                        handleRequest(
                                player,
                                requestDraft,
                                message
                        );

                        return;
                    }

                    handleOffer(
                            player,
                            offerDraft,
                            message
                    );
                }
        );
    }

    private void handleRequest(
            Player player,
            RequestDraft draft,
            String message
    ) {

        if (isCancel(message)) {

            requestDrafts.remove(
                    player.getUniqueId()
            );

            BusinessMessages.info(
                    player,
                    "Demandes " + BusinessMessages.brand(),
                    "Saisie annulée."
            );

            return;
        }

        if (draft.step == 0) {

            draft.title = message.trim();
            draft.step = 1;

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fDécris ta demande.");
            player.sendMessage("§7Indique le style, la quantité, le lieu ou les détails.");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 1) {

            draft.description = message.trim();
            draft.step = 2;

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fIndique ton budget.");
            player.sendMessage("§7Exemple: §e25000");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 2) {

            try {

                draft.budget =
                        Double.parseDouble(
                                message.replace(",", ".")
                        );

            } catch (Exception e) {

                BusinessMessages.deny(
                        player,
                        "Demandes " + BusinessMessages.brand(),
                        "Budget invalide. Écris un nombre."
                );

                return;
            }

            draft.step = 3;

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fIndique le délai souhaité en jours.");
            player.sendMessage("§7Exemple: §e7");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 3) {

            try {

                draft.dueDays =
                        Integer.parseInt(message);

            } catch (Exception e) {

                BusinessMessages.deny(
                        player,
                        "Demandes " + BusinessMessages.brand(),
                        "Délai invalide. Écris un nombre de jours."
                );

                return;
            }

            RequestManager.RequestResult result =
                    RequestManager.createRequest(
                            player,
                            draft.category,
                            draft.title,
                            draft.description,
                            draft.budget,
                            draft.dueDays
                    );

            requestDrafts.remove(
                    player.getUniqueId()
            );

            if (!result.success()) {

                BusinessMessages.deny(
                        player,
                        "Demandes " + BusinessMessages.brand(),
                        result.message()
                );

                return;
            }

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fDemande publiée avec succès.");
            player.sendMessage("§7Titre: §e" + result.request().getTitle());
            player.sendMessage("§7Budget: §e" + VaultHook.format(result.request().getBudget()));
            player.sendMessage("§7Délai: §b" + result.request().getDueDays() + " jours");
            player.sendMessage("§a✔ Le registre économique a été mis à jour.");

            BusinessMessages.footer(player);
        }
    }

    private void handleOffer(
            Player player,
            OfferDraft draft,
            String message
    ) {

        if (isCancel(message)) {

            offerDrafts.remove(
                    player.getUniqueId()
            );

            BusinessMessages.info(
                    player,
                    "Offre Entreprise",
                    "Saisie annulée."
            );

            return;
        }

        if (draft.step == 0) {

            try {

                draft.amount =
                        Double.parseDouble(
                                message.replace(",", ".")
                        );

            } catch (Exception e) {

                BusinessMessages.deny(
                        player,
                        "Offre Entreprise",
                        "Montant invalide. Écris un nombre."
                );

                return;
            }

            draft.step = 1;

            BusinessMessages.header(
                    player,
                    "Offre Entreprise"
            );

            player.sendMessage("§fIndique le délai proposé en jours.");
            player.sendMessage("§7Exemple: §e5");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 1) {

            try {

                draft.dueDays =
                        Integer.parseInt(message);

            } catch (Exception e) {

                BusinessMessages.deny(
                        player,
                        "Offre Entreprise",
                        "Délai invalide. Écris un nombre de jours."
                );

                return;
            }

            draft.step = 2;

            BusinessMessages.header(
                    player,
                    "Offre Entreprise"
            );

            player.sendMessage("§fAjoute un commentaire à l'offre.");
            player.sendMessage("§7Exemple: Livraison complète avec intérieur inclus.");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 2) {

            draft.comment = message;

            BusinessRequest request =
                    RequestManager.get(
                            draft.requestId
                    );

            Business business =
                    BusinessManager.getMemberBusiness(
                            player.getUniqueId()
                    );

            OfferManager.OfferResult result =
                    OfferManager.createOffer(
                            player,
                            business,
                            request,
                            draft.amount,
                            draft.dueDays,
                            draft.comment
                    );

            offerDrafts.remove(
                    player.getUniqueId()
            );

            if (!result.success()) {

                BusinessMessages.deny(
                        player,
                        "Offre Entreprise",
                        result.message()
                );

                return;
            }

            BusinessMessages.header(
                    player,
                    "Offre Entreprise"
            );

            player.sendMessage("§fOffre envoyée avec succès.");
            player.sendMessage("§7Montant: §e" + VaultHook.format(result.offer().getAmount()));
            player.sendMessage("§7Délai: §b" + result.offer().getDueDays() + " jours");
            player.sendMessage("§a✔ Le demandeur peut consulter votre proposition.");

            BusinessMessages.footer(player);
        }
    }

    private boolean isCancel(
            String message
    ) {

        return message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel");
    }

    private static class RequestDraft {

        private final RequestCategory category;

        private int step = 0;

        private String title = "";
        private String description = "";
        private double budget = 0;
        private int dueDays = 7;

        private RequestDraft(
                RequestCategory category
        ) {

            this.category = category;
        }
    }

    private static class OfferDraft {

        private final String requestId;

        private int step = 0;

        private double amount = 0;
        private int dueDays = 7;
        private String comment = "";

        private OfferDraft(
                String requestId
        ) {

            this.requestId = requestId;
        }
    }
}