package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Contract;

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

public class ContractChatListener implements Listener {

    private static final Map<UUID, Draft> drafts =
            new HashMap<>();

    private static final long TIMEOUT_TICKS =
            20L * 60L;

    public static void startComplete(
            Player player,
            Contract contract
    ) {

        start(
                player,
                contract,
                Mode.COMPLETE,
                "Contrat Officiel",
                "§e➜ §fÉcris un commentaire de fin.",
                "§8• §7Exemple : §eConstruction terminée avec intérieur complet."
        );
    }

    public static void startLitige(
            Player player,
            Contract contract
    ) {

        start(
                player,
                contract,
                Mode.LITIGE,
                "Litige Économique",
                "§e➜ §fExplique la raison du litige.",
                "§8• §7Les fonds resteront bloqués."
        );
    }

    private static void start(
            Player player,
            Contract contract,
            Mode mode,
            String module,
            String mainLine,
            String detailLine
    ) {

        if (player == null || contract == null || mode == null) {
            return;
        }

        Draft draft =
                new Draft(
                        contract.getId(),
                        mode
                );

        drafts.put(
                player.getUniqueId(),
                draft
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                module
        );

        player.sendMessage(mainLine);
        player.sendMessage(detailLine);
        player.sendMessage("§8• §7Minimum : §e5 caractères");
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
                            module,
                            "Saisie annulée : temps écoulé."
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

        String module =
                draft.mode == Mode.COMPLETE
                        ? "Contrat Officiel"
                        : "Litige Économique";

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            drafts.remove(
                    player.getUniqueId()
            );

            BusinessMessages.info(
                    player,
                    module,
                    "Saisie annulée."
            );

            return;
        }

        String comment =
                message.trim();

        if (comment.length() < 5) {

            BusinessMessages.deny(
                    player,
                    module,
                    "Message trop court. Ajoute quelques détails."
            );

            return;
        }

        Contract contract =
                ContractManager.get(
                        draft.contractId
                );

        drafts.remove(
                player.getUniqueId()
        );

        if (contract == null) {

            BusinessMessages.deny(
                    player,
                    module,
                    "Contrat introuvable."
            );

            return;
        }

        ContractManager.ContractResult result;

        if (draft.mode == Mode.COMPLETE) {

            result =
                    ContractManager.complete(
                            player,
                            contract,
                            comment
                    );

        } else {

            result =
                    ContractManager.openLitige(
                            player,
                            contract,
                            comment
                    );
        }

        if (!result.success()) {

            BusinessMessages.deny(
                    player,
                    module,
                    result.message()
            );

            return;
        }

        BusinessMessages.success(
                player,
                module,
                result.message()
        );
    }

    private enum Mode {

        COMPLETE,
        LITIGE
    }

    private static class Draft {

        private final String contractId;
        private final Mode mode;

        private Draft(
                String contractId,
                Mode mode
        ) {

            this.contractId = contractId;
            this.mode = mode;
        }
    }
}
