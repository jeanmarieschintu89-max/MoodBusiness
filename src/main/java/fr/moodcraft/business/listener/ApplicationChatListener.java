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
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApplicationChatListener implements Listener {

    private static final Map<UUID, Draft> drafts =
            new HashMap<>();

    private static final long TIMEOUT_TICKS =
            20L * 60L;

    public static void start(
            Player player,
            Business business,
            ApplicationType type
    ) {

        if (player == null || business == null || type == null) {
            return;
        }

        Draft draft =
                new Draft(
                        business.getId(),
                        type
                );

        drafts.put(
                player.getUniqueId(),
                draft
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                "Candidature " + BusinessMessages.brand()
        );

        player.sendMessage("§e➜ §fÉcris une courte présentation.");
        player.sendMessage("§8• §7Exemple : §eJe souhaite apprendre la construction médiévale.");
        player.sendMessage("§8• §7Minimum : §e10 caractères");
        player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");
        player.sendMessage("§8• §7Annulation auto dans §e60 secondes");

        BusinessMessages.footer(player);

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> {

                    Draft current =
                            drafts.get(
                                    player.getUniqueId()
                            );

                    if (current == null || current != draft) {
                        return;
                    }

                    drafts.remove(
                            player.getUniqueId()
                    );

                    if (!player.isOnline()) {
                        return;
                    }

                    BusinessMessages.info(
                            player,
                            "Candidature " + BusinessMessages.brand(),
                            "Candidature annulée : temps écoulé."
                    );
                },
                TIMEOUT_TICKS
        );
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {

        drafts.remove(
                event.getPlayer().getUniqueId()
        );
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

            String presentation =
                    message.trim();

            if (presentation.length() < 10) {

                BusinessMessages.deny(
                        player,
                        "Candidature " + BusinessMessages.brand(),
                        "Présentation trop courte. Ajoute quelques détails."
                );

                return;
            }

            draft.presentation =
                    presentation;

            draft.step = 1;

            BusinessMessages.header(
                    player,
                    "Candidature " + BusinessMessages.brand()
            );

            player.sendMessage("§e➜ §fIndique tes disponibilités.");
            player.sendMessage("§8• §7Exemple : §esoir et week-end.");
            player.sendMessage("§8• §7Minimum : §e3 caractères");
            player.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(player);

            return;
        }

        if (draft.step == 1) {

            String availability =
                    message.trim();

            if (availability.length() < 3) {

                BusinessMessages.deny(
                        player,
                        "Candidature " + BusinessMessages.brand(),
                        "Disponibilités trop courtes. Exemple : §esoir§7."
                );

                return;
            }

            draft.availability =
                    availability;

            Business business =
                    BusinessManager.getById(
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

            player.sendMessage("§a✔ §fCandidature envoyée avec succès.");
            player.sendMessage("§8• §7Entreprise : §e" + business.getName());
            player.sendMessage("§8• §7Type : " + draft.type.getDisplayName());
            player.sendMessage("§8• §7Le dossier est transmis à l'entreprise.");

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
