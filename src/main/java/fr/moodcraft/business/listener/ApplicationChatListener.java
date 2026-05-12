package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.ApplicationManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.ApplicationType;
import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApplicationChatListener implements Listener {

    private static final Map<UUID, Draft> drafts =
            new HashMap<>();

    public static void start(
            Player player,
            Business business,
            ApplicationType type
    ) {

        drafts.put(
                player.getUniqueId(),
                new Draft(
                        business.getId(),
                        type
                )
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                "Candidature " + BusinessMessages.brand()
        );

        player.sendMessage("§fÉcris une courte présentation.");
        player.sendMessage("§7Exemple: Je souhaite apprendre la construction médiévale.");
        player.sendMessage("§7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(player);
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player player =
                e.getPlayer();

        Draft draft =
                drafts.get(
                        player.getUniqueId()
                );

        if (draft == null) {
            return;
        }

        e.setCancelled(true);

        String message =
                e.getMessage();

        Bukkit.getScheduler().runTask(
                Main.getInstance(),
                () -> handle(
                        player,
                        draft,
                        message
                )
        );
    }

    private void handle(
            Player player,
            Draft draft,
            String message
    ) {

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            drafts.remove(
                    player.getUniqueId()
            );

            BusinessMessages.info(
                    player,
                    "Candidature " + BusinessMessages.brand(),
                    "Saisie annulée."
            );

            return;
        }

        if (draft.step == 0) {

            draft.presentation =
                    message;

            draft.step = 1;

            BusinessMessages.header(
                    player,
                    "Candidature " + BusinessMessages.brand()
            );

            player.sendMessage("§fIndique tes disponibilités.");
            player.sendMessage("§7Exemple: soir et week-end.");
            player.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 1) {

            draft.availability =
                    message;

            Business business =
                    BusinessManager.getByName(
                            draft.businessId
                    );

            if (business == null) {

                drafts.remove(
                        player.getUniqueId()
                );

                BusinessMessages.deny(
                        player,
                        "Candidature " + BusinessMessages.brand(),
                        "Entreprise introuvable."
                );

                return;
            }

            ApplicationManager.ApplicationResult result =
                    ApplicationManager.createApplication(
                            player,
                            business,
                            draft.type,
                            draft.presentation,
                            draft.availability
                    );

            drafts.remove(
                    player.getUniqueId()
            );

            if (!result.success()) {

                BusinessMessages.deny(
                        player,
                        "Candidature " + BusinessMessages.brand(),
                        result.message()
                );

                return;
            }

            BusinessMessages.header(
                    player,
                    "Candidature " + BusinessMessages.brand()
            );

            player.sendMessage("§fCandidature envoyée avec succès.");
            player.sendMessage("§7Entreprise: §e" + business.getName());
            player.sendMessage("§7Type: " + draft.type.getDisplayName());
            player.sendMessage("§a✔ Le dossier est transmis à l'entreprise.");

            BusinessMessages.footer(player);
        }
    }

    private static class Draft {

        private final String businessId;
        private final ApplicationType type;

        private int step = 0;

        private String presentation = "";
        private String availability = "";

        private Draft(
                String businessId,
                ApplicationType type
        ) {

            this.businessId = businessId;
            this.type = type;
        }
    }
}