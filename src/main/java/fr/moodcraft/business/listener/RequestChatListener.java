package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.RequestManager;

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

    private static final Map<UUID, RequestDraft> requestDrafts = new HashMap<>();

    public static void startRequest(Player player, RequestCategory category) {
        requestDrafts.put(player.getUniqueId(), new RequestDraft(category));
        player.closeInventory();

        BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
        player.sendMessage("§fÉcris le titre de ta mission.");
        player.sendMessage("§7Exemple: §eMaison médiévale à Utopia");
        player.sendMessage("");
        player.sendMessage("§8• §7Minimum: §e3 caractères");
        player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");
        BusinessMessages.footer(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        RequestDraft requestDraft = requestDrafts.get(player.getUniqueId());

        if (requestDraft == null) return;

        e.setCancelled(true);
        String message = e.getMessage();

        Bukkit.getScheduler().runTask(Main.getInstance(), () -> handleRequest(player, requestDraft, message));
    }

    private void handleRequest(Player player, RequestDraft draft, String message) {
        if (isCancel(message)) {
            requestDrafts.remove(player.getUniqueId());
            BusinessMessages.info(player, "Missions " + BusinessMessages.brand(), "Saisie annulée.");
            return;
        }

        if (draft.step == 0) {
            String title = message.trim();
            if (title.length() < 3) {
                BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
                player.sendMessage("§cTitre trop court.");
                player.sendMessage("§7Écris un titre plus clair.");
                player.sendMessage("§8Exemple: §eMaison médiévale à Utopia");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");
                BusinessMessages.footer(player);
                return;
            }

            draft.title = title;
            draft.step = 1;

            BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
            player.sendMessage("§fDécris ta mission.");
            player.sendMessage("§7Indique le style, la quantité, le lieu ou les détails.");
            player.sendMessage("");
            player.sendMessage("§8• §7Minimum: §e10 caractères");
            player.sendMessage("§8• §7Exemple: §eMaison médiévale avec intérieur et jardin.");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");
            BusinessMessages.footer(player);
            return;
        }

        if (draft.step == 1) {
            String description = message.trim();
            if (description.length() < 10) {
                BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
                player.sendMessage("§cDescription trop courte.");
                player.sendMessage("§7Ajoute plus de détails pour les entreprises.");
                player.sendMessage("");
                player.sendMessage("§8Exemple: §eMaison médiévale avec intérieur, jardin et stockage.");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");
                BusinessMessages.footer(player);
                return;
            }

            draft.description = description;
            draft.step = 2;

            BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
            player.sendMessage("§fIndique ton budget.");
            player.sendMessage("§7Exemple: §e25000");
            player.sendMessage("");
            player.sendMessage("§8• §7Montant en euros");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");
            BusinessMessages.footer(player);
            return;
        }

        if (draft.step == 2) {
            double budget;
            try {
                budget = Double.parseDouble(message.replace(",", "."));
            } catch (Exception e) {
                BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
                player.sendMessage("§cBudget invalide.");
                player.sendMessage("§7Écris seulement un nombre.");
                player.sendMessage("§8Exemple: §e25000");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");
                BusinessMessages.footer(player);
                return;
            }

            if (budget <= 0) {
                BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
                player.sendMessage("§cBudget invalide.");
                player.sendMessage("§7Le budget doit être supérieur à zéro.");
                player.sendMessage("§8Exemple: §e25000");
                BusinessMessages.footer(player);
                return;
            }

            draft.budget = budget;
            draft.step = 3;

            BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
            player.sendMessage("§fIndique le délai souhaité en jours.");
            player.sendMessage("§7Exemple: §e7");
            player.sendMessage("");
            player.sendMessage("§8• §7Nombre de jours uniquement");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");
            BusinessMessages.footer(player);
            return;
        }

        if (draft.step == 3) {
            int dueDays;
            try {
                dueDays = Integer.parseInt(message);
            } catch (Exception e) {
                BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
                player.sendMessage("§cDélai invalide.");
                player.sendMessage("§7Écris un nombre de jours.");
                player.sendMessage("§8Exemple: §e7");
                player.sendMessage("");
                player.sendMessage("§7Tape §cannuler §7pour quitter.");
                BusinessMessages.footer(player);
                return;
            }

            if (dueDays <= 0) {
                BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
                player.sendMessage("§cDélai invalide.");
                player.sendMessage("§7Le délai doit être supérieur à zéro.");
                player.sendMessage("§8Exemple: §e7");
                BusinessMessages.footer(player);
                return;
            }

            draft.dueDays = dueDays;

            RequestManager.RequestResult result = RequestManager.createRequest(
                    player,
                    draft.category,
                    draft.title,
                    draft.description,
                    draft.budget,
                    draft.dueDays
            );

            requestDrafts.remove(player.getUniqueId());

            if (!result.success()) {
                BusinessMessages.deny(player, "Missions " + BusinessMessages.brand(), result.message());
                return;
            }

            BusinessMessages.header(player, "Missions " + BusinessMessages.brand());
            player.sendMessage("§fMission publiée avec succès.");
            player.sendMessage("§7Titre: §e" + result.request().getTitle());
            player.sendMessage("§7Budget: §e" + VaultHook.format(result.request().getBudget()));
            player.sendMessage("§7Délai: §b" + result.request().getDueDays() + " jours");
            player.sendMessage("§7Catégorie: " + result.request().getCategory().getDisplayName());
            player.sendMessage("");
            player.sendMessage("§a✔ Les entreprises peuvent maintenant la prendre.");
            BusinessMessages.footer(player);
        }
    }

    private boolean isCancel(String message) {
        return message.equalsIgnoreCase("annuler") || message.equalsIgnoreCase("cancel");
    }

    private static class RequestDraft {
        private final RequestCategory category;
        private int step = 0;
        private String title = "";
        private String description = "";
        private double budget = 0;
        private int dueDays = 7;

        private RequestDraft(RequestCategory category) {
            this.category = category;
        }
    }
}
