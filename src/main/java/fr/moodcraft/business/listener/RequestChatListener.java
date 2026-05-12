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
        player.sendMessage("§7Exemple: §eMaison médiévale à Utopia");
        player.sendMessage("");
        player.sendMessage("§8• §7Minimum: §e3 caractères");
        player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

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
        player.sendMessage("");
        player.sendMessage("§8• §7Exemple: §e25000");
        player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

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

        //
        // 1. TITRE
        //

        if (draft.step == 0) {

            String title =
                    message.trim();

            if (title.length() < 3) {

                BusinessMessages.header(
                        player,
                        "Demandes " + BusinessMessages.brand()
                );

                player.sendMessage("§cTitre trop court.");
                player.sendMessage("§7Écris un titre plus clair.");
                player.sendMessage("§8Exemple: §eMaison médiévale à Utopia");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");

                BusinessMessages.footer(player);

                return;
            }

            draft.title =
                    title;

            draft.step =
                    1;

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fDécris ta demande.");
            player.sendMessage("§7Indique le style, la quantité, le lieu ou les détails.");
            player.sendMessage("");
            player.sendMessage("§8• §7Minimum: §e10 caractères");
            player.sendMessage("§8• §7Exemple: §eMaison médiévale avec intérieur et jardin.");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        //
        // 2. DESCRIPTION
        //

        if (draft.step == 1) {

            String description =
                    message.trim();

            if (description.length() < 10) {

                BusinessMessages.header(
                        player,
                        "Demandes " + BusinessMessages.brand()
                );

                player.sendMessage("§cDescription trop courte.");
                player.sendMessage("§7Ajoute plus de détails pour les entreprises.");
                player.sendMessage("");
                player.sendMessage("§8Exemple:");
                player.sendMessage("§eMaison médiévale avec intérieur, jardin et stockage.");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");

                BusinessMessages.footer(player);

                return;
            }

            draft.description =
                    description;

            draft.step =
                    2;

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fIndique ton budget.");
            player.sendMessage("§7Exemple: §e25000");
            player.sendMessage("");
            player.sendMessage("§8• §7Montant en euros");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        //
        // 3. BUDGET
        //

        if (draft.step == 2) {

            double budget;

            try {

                budget =
                        Double.parseDouble(
                                message.replace(",", ".")
                        );

            } catch (Exception e) {

                BusinessMessages.header(
                        player,
                        "Demandes " + BusinessMessages.brand()
                );

                player.sendMessage("§cBudget invalide.");
                player.sendMessage("§7Écris seulement un nombre.");
                player.sendMessage("§8Exemple: §e25000");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");

                BusinessMessages.footer(player);

                return;
            }

            if (budget <= 0) {

                BusinessMessages.header(
                        player,
                        "Demandes " + BusinessMessages.brand()
                );

                player.sendMessage("§cBudget invalide.");
                player.sendMessage("§7Le budget doit être supérieur à zéro.");
                player.sendMessage("§8Exemple: §e25000");

                BusinessMessages.footer(player);

                return;
            }

            draft.budget =
                    budget;

            draft.step =
                    3;

            BusinessMessages.header(
                    player,
                    "Demandes " + BusinessMessages.brand()
            );

            player.sendMessage("§fIndique le délai souhaité en jours.");
            player.sendMessage("§7Exemple: §e7");
            player.sendMessage("");
            player.sendMessage("§8• §7Nombre de jours uniquement");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        //
        // 4. DELAI
        //

        if (draft.step == 3) {

            int dueDays;

            try {

                dueDays =
                        Integer.parseInt(message);

            } catch (Exception e) {

                BusinessMessages.header(
                        player,
                        "Demandes " + BusinessMessages.brand()
                );

                player.sendMessage("§cDélai invalide.");
                player.sendMessage("§7Écris un nombre de jours.");
                player.sendMessage("§8Exemple: §e7");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");

                BusinessMessages.footer(player);

                return;
            }

            if (dueDays <= 0) {

                BusinessMessages.header(
                        player,
                        "Demandes " + BusinessMessages.brand()
                );

                player.sendMessage("§cDélai invalide.");
                player.sendMessage("§7Le délai doit être supérieur à zéro.");
                player.sendMessage("§8Exemple: §e7");

                BusinessMessages.footer(player);

                return;
            }

            draft.dueDays =
                    dueDays;

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
            player.sendMessage("§7Catégorie: " + result.request().getCategory().getDisplayName());
            player.sendMessage("");
            player.sendMessage("§a✔ Le Bureau des Entreprises a été mis à jour.");

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

        //
        // 1. MONTANT
        //

        if (draft.step == 0) {

            double amount;

            try {

                amount =
                        Double.parseDouble(
                                message.replace(",", ".")
                        );

            } catch (Exception e) {

                BusinessMessages.header(
                        player,
                        "Offre Entreprise"
                );

                player.sendMessage("§cMontant invalide.");
                player.sendMessage("§7Écris seulement un nombre.");
                player.sendMessage("§8Exemple: §e25000");

                BusinessMessages.footer(player);

                return;
            }

            if (amount <= 0) {

                BusinessMessages.header(
                        player,
                        "Offre Entreprise"
                );

                player.sendMessage("§cMontant invalide.");
                player.sendMessage("§7Le montant doit être supérieur à zéro.");

                BusinessMessages.footer(player);

                return;
            }

            draft.amount =
                    amount;

            draft.step =
                    1;

            BusinessMessages.header(
                    player,
                    "Offre Entreprise"
            );

            player.sendMessage("§fIndique le délai proposé en jours.");
            player.sendMessage("§7Exemple: §e5");
            player.sendMessage("");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        //
        // 2. DELAI
        //

        if (draft.step == 1) {

            int dueDays;

            try {

                dueDays =
                        Integer.parseInt(message);

            } catch (Exception e) {

                BusinessMessages.header(
                        player,
                        "Offre Entreprise"
                );

                player.sendMessage("§cDélai invalide.");
                player.sendMessage("§7Écris un nombre de jours.");
                player.sendMessage("§8Exemple: §e5");

                BusinessMessages.footer(player);

                return;
            }

            if (dueDays <= 0) {

                BusinessMessages.header(
                        player,
                        "Offre Entreprise"
                );

                player.sendMessage("§cDélai invalide.");
                player.sendMessage("§7Le délai doit être supérieur à zéro.");

                BusinessMessages.footer(player);

                return;
            }

            draft.dueDays =
                    dueDays;

            draft.step =
                    2;

            BusinessMessages.header(
                    player,
                    "Offre Entreprise"
            );

            player.sendMessage("§fAjoute un commentaire à l'offre.");
            player.sendMessage("§7Exemple: Livraison complète avec intérieur inclus.");
            player.sendMessage("");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        //
        // 3. COMMENTAIRE
        //

        if (draft.step == 2) {

            String comment =
                    message.trim();

            if (comment.length() < 5) {

                BusinessMessages.header(
                        player,
                        "Offre Entreprise"
                );

                player.sendMessage("§cCommentaire trop court.");
                player.sendMessage("§7Ajoute une petite précision pour le demandeur.");
                player.sendMessage("§8Exemple: §eTravail complet avec matériaux inclus.");

                BusinessMessages.footer(player);

                return;
            }

            draft.comment =
                    comment;

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
            player.sendMessage("");
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